#!/usr/bin/env python3
"""
Bahai Document Processor
Extracts text from PDFs and creates searchable index for Android app
"""

import os
import json
import sqlite3
import hashlib
import re
from pathlib import Path
from datetime import datetime
import argparse

try:
    import PyPDF2
    HAS_PYPDF2 = True
except ImportError:
    HAS_PYPDF2 = False
    
try:
    from pdfplumber import PDF
    HAS_PDFPLUMBER = True
except ImportError:
    HAS_PDFPLUMBER = False

class BahaiDocumentProcessor:
    def __init__(self, documents_dir="documents"):
        self.documents_dir = Path(documents_dir)
        self.confirmed_dir = self.documents_dir / "confirmed-official"
        self.pending_dir = self.documents_dir / "pending-permissions"
        
        # Output directory for processed data
        self.output_dir = Path("android-app/app/src/main/assets/database")
        self.output_dir.mkdir(parents=True, exist_ok=True)
        
        # Database for search indexing
        self.db_path = self.output_dir / "bahai_documents.db"
        
        # Text extraction output
        self.text_dir = Path("processed_text")
        self.text_dir.mkdir(exist_ok=True)
        
        # Load metadata
        self.metadata_file = self.documents_dir / "document_metadata.json"
        self.load_metadata()
        
        # Initialize database
        self.init_database()
    
    def load_metadata(self):
        """Load document metadata"""
        if self.metadata_file.exists():
            with open(self.metadata_file, 'r', encoding='utf-8') as f:
                self.metadata = json.load(f)
        else:
            self.metadata = {'documents': {}}
            print("Warning: No metadata file found. Run document_downloader.py first.")
    
    def init_database(self):
        """Initialize SQLite database with FTS5 for full-text search"""
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        
        # Create documents table
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS documents (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                author TEXT NOT NULL,
                category TEXT NOT NULL,
                description TEXT,
                file_path TEXT NOT NULL,
                file_hash TEXT NOT NULL,
                page_count INTEGER,
                word_count INTEGER,
                extracted_date TEXT,
                source_url TEXT,
                UNIQUE(file_hash)
            )
        ''')
        
        # Create pages table for individual page content
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS pages (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                document_id INTEGER NOT NULL,
                page_number INTEGER NOT NULL,
                page_text TEXT NOT NULL,
                word_count INTEGER,
                FOREIGN KEY(document_id) REFERENCES documents(id),
                UNIQUE(document_id, page_number)
            )
        ''')
        
        # Create FTS5 virtual table for full-text search
        cursor.execute('''
            CREATE VIRTUAL TABLE IF NOT EXISTS document_search USING fts5(
                title,
                author, 
                content,
                category
            )
        ''')
        
        # Create search terms table for auto-complete
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS search_terms (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                term TEXT UNIQUE NOT NULL,
                frequency INTEGER DEFAULT 1,
                category TEXT
            )
        ''')
        
        # Create bookmarks/favorites table (for future use)
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS bookmarks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                document_id INTEGER NOT NULL,
                page_number INTEGER,
                bookmark_text TEXT,
                note TEXT,
                created_date TEXT,
                FOREIGN KEY(document_id) REFERENCES documents(id)
            )
        ''')
        
        conn.commit()
        conn.close()
        print(f"Database initialized: {self.db_path}")
    
    def extract_text_from_file(self, file_path):
        """Extract text from file (PDF or text)"""
        file_extension = file_path.suffix.lower()
        
        if file_extension == '.txt':
            # Handle text files
            try:
                with open(file_path, 'r', encoding='utf-8') as f:
                    content = f.read()
                    
                # Split into pages (for text files, we'll treat each paragraph as a "page")
                paragraphs = [p.strip() for p in content.split('\n\n') if p.strip()]
                text_pages = []
                
                for page_num, paragraph in enumerate(paragraphs, 1):
                    text = self.clean_text(paragraph)
                    if text:  # Only add non-empty pages
                        text_pages.append({
                            'page': page_num,
                            'text': text,
                            'word_count': len(text.split())
                        })
                
                return text_pages
                
            except Exception as e:
                print(f"Error reading text file {file_path}: {e}")
                return []
                
        elif file_extension == '.pdf':
            # Handle PDF files
            return self.extract_text_from_pdf(file_path)
        
        else:
            print(f"Unsupported file type: {file_extension}")
            return []
        """Extract text from PDF using available libraries"""
        text_pages = []
        
        try:
            # Try pdfplumber first (usually better text extraction)
            if HAS_PDFPLUMBER:
                import pdfplumber
                with pdfplumber.open(pdf_path) as pdf:
                    for page_num, page in enumerate(pdf.pages, 1):
                        text = page.extract_text() or ""
                        text = self.clean_text(text)
                        text_pages.append({
                            'page': page_num,
                            'text': text,
                            'word_count': len(text.split())
                        })
                        
            # Fallback to PyPDF2
            elif HAS_PYPDF2:
                with open(pdf_path, 'rb') as file:
                    pdf_reader = PyPDF2.PdfReader(file)
                    for page_num, page in enumerate(pdf_reader.pages, 1):
                        text = page.extract_text() or ""
                        text = self.clean_text(text)
                        text_pages.append({
                            'page': page_num,
                            'text': text,
                            'word_count': len(text.split())
                        })
            else:
                print("Warning: No PDF processing libraries available.")
                print("Install: pip install pdfplumber PyPDF2")
                return []
                
        except Exception as e:
            print(f"Error extracting text from {pdf_path}: {e}")
            return []
        
        return text_pages
    
    def clean_text(self, text):
        """Clean and normalize extracted text"""
        if not text:
            return ""
        
        # Remove excessive whitespace
        text = re.sub(r'\s+', ' ', text)
        
        # Remove page headers/footers (basic heuristics)
        lines = text.split('\n')
        cleaned_lines = []
        
        for line in lines:
            line = line.strip()
            # Skip likely headers/footers
            if len(line) < 3:
                continue
            if re.match(r'^\d+$', line):  # Skip page numbers
                continue
            if 'www.' in line.lower() or 'http' in line.lower():  # Skip URLs
                continue
            cleaned_lines.append(line)
        
        text = '\n'.join(cleaned_lines)
        text = text.strip()
        
        return text
    
    def extract_search_terms(self, text, min_length=3, max_terms=100):
        """Extract important search terms from text"""
        # Common Bahai-specific terms to prioritize
        bahai_terms = {
            'bahaullah', 'abdul-baha', 'shoghi', 'effendi', 'universal', 'house', 'justice',
            'kitab', 'aqdas', 'iqan', 'gleanings', 'manifestation', 'covenant', 'administrative',
            'spiritual', 'assembly', 'guardian', 'guidance', 'revelation', 'dispensation',
            'bahai', 'faith', 'unity', 'diversity', 'peace', 'justice', 'love', 'service'
        }
        
        # Extract words
        words = re.findall(r'\b[a-zA-Z]{' + str(min_length) + ',}\b', text.lower())
        
        # Count frequency
        word_freq = {}
        for word in words:
            word_freq[word] = word_freq.get(word, 0) + 1
        
        # Prioritize Bahai-specific terms
        for term in bahai_terms:
            if term in word_freq:
                word_freq[term] *= 3  # Boost Bahai-specific terms
        
        # Sort by frequency and return top terms
        sorted_terms = sorted(word_freq.items(), key=lambda x: x[1], reverse=True)
        return sorted_terms[:max_terms]
    
    def process_document(self, file_path):
        """Process a single PDF document"""
        try:
            print(f"Processing: {file_path.name}")
            
            # Calculate file hash
            file_hash = self.calculate_file_hash(file_path)
            
            # Check if already processed
            conn = sqlite3.connect(self.db_path)
            cursor = conn.cursor()
            cursor.execute("SELECT id FROM documents WHERE file_hash = ?", (file_hash,))
            existing = cursor.fetchone()
            
            if existing:
                print(f"  Already processed: {file_path.name}")
                conn.close()
                return existing[0]
            
            # Find metadata for this file
            doc_meta = None
            for url, meta in self.metadata.get('documents', {}).items():
                if Path(meta.get('filepath', '')).name == file_path.name:
                    doc_meta = meta
                    break
            
            if not doc_meta:
                print(f"  Warning: No metadata found for {file_path.name}")
                doc_meta = {
                    'title': file_path.stem,
                    'author': 'Unknown',
                    'category': 'Document',
                    'description': '',
                    'source': 'Local'
                }
            
            # Extract text from file (PDF or text)
            text_pages = self.extract_text_from_file(file_path)
            
            if not text_pages:
                print(f"  Failed to extract text from {file_path.name}")
                conn.close()
                return None
            
            # Calculate totals
            total_words = sum(page['word_count'] for page in text_pages)
            page_count = len(text_pages)
            
            # Insert document record
            cursor.execute('''
                INSERT INTO documents 
                (title, author, category, description, file_path, file_hash, 
                 page_count, word_count, extracted_date, source_url)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ''', (
                doc_meta['title'],
                doc_meta['author'], 
                doc_meta['category'],
                doc_meta.get('description', ''),
                str(file_path.relative_to(self.documents_dir)),
                file_hash,
                page_count,
                total_words,
                datetime.now().isoformat(),
                doc_meta.get('url', '')
            ))
            
            document_id = cursor.lastrowid
            
            # Insert page records and build full text for search
            full_text_parts = []
            
            for page_data in text_pages:
                cursor.execute('''
                    INSERT INTO pages (document_id, page_number, page_text, word_count)
                    VALUES (?, ?, ?, ?)
                ''', (
                    document_id,
                    page_data['page'],
                    page_data['text'],
                    page_data['word_count']
                ))
                
                full_text_parts.append(page_data['text'])
            
            # Insert into FTS table
            full_text = '\n'.join(full_text_parts)
            cursor.execute('''
                INSERT INTO document_search (rowid, title, author, content, category)
                VALUES (?, ?, ?, ?, ?)
            ''', (
                document_id,
                doc_meta['title'],
                doc_meta['author'],
                full_text,
                doc_meta['category']
            ))
            
            # Extract and store search terms
            search_terms = self.extract_search_terms(full_text)
            for term, frequency in search_terms:
                cursor.execute('''
                    INSERT OR REPLACE INTO search_terms (term, frequency, category)
                    VALUES (?, ?, ?)
                ''', (term, frequency, doc_meta['category']))
            
            conn.commit()
            conn.close()
            
            # Save extracted text to file for backup
            text_file = self.text_dir / f"{file_path.stem}.txt"
            with open(text_file, 'w', encoding='utf-8') as f:
                f.write(f"Title: {doc_meta['title']}\n")
                f.write(f"Author: {doc_meta['author']}\n")
                f.write(f"Category: {doc_meta['category']}\n")
                f.write(f"Pages: {page_count}\n")
                f.write(f"Words: {total_words}\n")
                f.write("=" * 60 + "\n\n")
                f.write(full_text)
            
            print(f"  Extracted: {page_count} pages, {total_words} words")
            return document_id
            
        except Exception as e:
            print(f"Error processing {file_path}: {e}")
            return None
    
    def calculate_file_hash(self, file_path):
        """Calculate SHA-256 hash of file"""
        hash_sha256 = hashlib.sha256()
        with open(file_path, "rb") as f:
            for chunk in iter(lambda: f.read(4096), b""):
                hash_sha256.update(chunk)
        return hash_sha256.hexdigest()
    
    def process_all_documents(self):
        """Process all documents in the documents directory"""
        text_files = []
        pdf_files = []
        
        # Find all text and PDF files
        for directory in [self.confirmed_dir, self.pending_dir]:
            if directory.exists():
                text_files.extend(directory.glob("*.txt"))
                pdf_files.extend(directory.glob("*.pdf"))
        
        all_files = text_files + pdf_files
        
        if not all_files:
            print("No text or PDF files found to process.")
            print("Run simple_downloader.py first to create sample documents.")
            return
        
        print(f"Found {len(text_files)} text files and {len(pdf_files)} PDF files to process")
        
        processed_count = 0
        failed_count = 0
        
        for file in all_files:
            result = self.process_document(file)
            if result:
                processed_count += 1
            else:
                failed_count += 1
        
        print(f"\nProcessing Summary:")
        print(f"  Successfully processed: {processed_count}")
        print(f"  Failed: {failed_count}")
        print(f"  Database: {self.db_path}")
        
        self.generate_processing_report()
    
    def generate_processing_report(self):
        """Generate a report of processed documents"""
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        
        # Get summary statistics
        cursor.execute("SELECT COUNT(*) FROM documents")
        doc_count = cursor.fetchone()[0]
        
        cursor.execute("SELECT SUM(page_count) FROM documents") 
        total_pages = cursor.fetchone()[0] or 0
        
        cursor.execute("SELECT SUM(word_count) FROM documents")
        total_words = cursor.fetchone()[0] or 0
        
        cursor.execute("SELECT COUNT(*) FROM search_terms")
        term_count = cursor.fetchone()[0]
        
        # Get documents by author
        cursor.execute('''
            SELECT author, COUNT(*), SUM(page_count), SUM(word_count)
            FROM documents 
            GROUP BY author 
            ORDER BY COUNT(*) DESC
        ''')
        by_author = cursor.fetchall()
        
        conn.close()
        
        # Write report
        report_file = self.output_dir / "processing_report.txt"
        with open(report_file, 'w', encoding='utf-8') as f:
            f.write("BAHAI RESOURCE LIBRARY - DOCUMENT PROCESSING REPORT\n")
            f.write("=" * 60 + "\n\n")
            f.write(f"Generated: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n\n")
            
            f.write("SUMMARY STATISTICS\n")
            f.write("-" * 20 + "\n")
            f.write(f"Total Documents: {doc_count}\n")
            f.write(f"Total Pages: {total_pages:,}\n") 
            f.write(f"Total Words: {total_words:,}\n")
            f.write(f"Search Terms: {term_count:,}\n\n")
            
            f.write("BY AUTHOR\n")
            f.write("-" * 15 + "\n")
            for author, docs, pages, words in by_author:
                f.write(f"{author}:\n")
                f.write(f"  Documents: {docs}\n")
                f.write(f"  Pages: {pages:,}\n")
                f.write(f"  Words: {words:,}\n\n")
            
            f.write("FILES GENERATED\n")
            f.write("-" * 20 + "\n")
            f.write(f"Database: {self.db_path}\n")
            f.write(f"Text Files: {self.text_dir}/*.txt\n")
            f.write(f"Report: {report_file}\n")
        
        print(f"Processing report saved: {report_file}")
        print(f"Ready for Android app integration!")

def main():
    """Main function"""
    parser = argparse.ArgumentParser(description='Process Bahai documents for search indexing')
    parser.add_argument('--documents-dir', default='documents', 
                       help='Directory containing PDF documents')
    parser.add_argument('--single-file', help='Process a single PDF file')
    
    args = parser.parse_args()
    
    print("Bahai Document Processor v0.2.0")
    print("=" * 40)
    
    # Check for required libraries
    if not HAS_PYPDF2 and not HAS_PDFPLUMBER:
        print("Error: No PDF processing libraries found.")
        print("Please install: pip install pdfplumber PyPDF2")
        return
    
    processor = BahaiDocumentProcessor(args.documents_dir)
    
    if args.single_file:
        processor.process_document(Path(args.single_file))
    else:
        processor.process_all_documents()

if __name__ == "__main__":
    main()