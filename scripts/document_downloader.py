#!/usr/bin/env python3
"""
Bahai Document Downloader
Downloads official Bahai texts from reference.bahai.org and other official sources
"""

import requests
import os
import json
import re
from urllib.parse import urljoin, urlparse
from pathlib import Path
import time
import hashlib

class BahaiDocumentDownloader:
    def __init__(self, base_dir="documents"):
        self.base_dir = Path(base_dir)
        self.confirmed_dir = self.base_dir / "confirmed-official"
        self.pending_dir = self.base_dir / "pending-permissions"
        
        # Create directories
        self.confirmed_dir.mkdir(parents=True, exist_ok=True)
        self.pending_dir.mkdir(parents=True, exist_ok=True)
        
        # Official Bahai websites that we can freely download from
        self.official_sources = {
            'reference.bahai.org': self.confirmed_dir,
            'www.bahai.org': self.confirmed_dir,
            'bahai.org': self.confirmed_dir,
            'universalhouseofjustice.bahai.org': self.confirmed_dir
        }
        
        # Document metadata tracking
        self.metadata_file = self.base_dir / "document_metadata.json"
        self.load_metadata()
        
        # Session for efficient downloading
        self.session = requests.Session()
        self.session.headers.update({
            'User-Agent': 'Bahai Resource Library Document Downloader 0.2.0'
        })

    def load_metadata(self):
        """Load existing document metadata"""
        if self.metadata_file.exists():
            with open(self.metadata_file, 'r', encoding='utf-8') as f:
                self.metadata = json.load(f)
        else:
            self.metadata = {
                'documents': {},
                'last_updated': None,
                'sources': list(self.official_sources.keys())
            }
    
    def save_metadata(self):
        """Save document metadata to file"""
        self.metadata['last_updated'] = time.strftime('%Y-%m-%d %H:%M:%S')
        with open(self.metadata_file, 'w', encoding='utf-8') as f:
            json.dump(self.metadata, f, indent=2, ensure_ascii=False)
    
    def get_file_hash(self, filepath):
        """Calculate SHA-256 hash of file"""
        hash_sha256 = hashlib.sha256()
        with open(filepath, "rb") as f:
            for chunk in iter(lambda: f.read(4096), b""):
                hash_sha256.update(chunk)
        return hash_sha256.hexdigest()
    
    def download_document(self, url, title, author, category, description=""):
        """Download a single document"""
        try:
            print(f"Downloading: {title} by {author}")
            
            # Determine target directory based on source
            parsed_url = urlparse(url)
            target_dir = self.official_sources.get(parsed_url.netloc, self.pending_dir)
            
            # Create filename
            safe_title = re.sub(r'[^\w\s-]', '', title).strip().replace(' ', '_')
            safe_author = re.sub(r'[^\w\s-]', '', author).strip().replace(' ', '_')
            filename = f"{safe_author}_{safe_title}.pdf"
            filepath = target_dir / filename
            
            # Skip if already downloaded and hash matches
            if filepath.exists() and url in self.metadata['documents']:
                stored_hash = self.metadata['documents'][url].get('hash')
                current_hash = self.get_file_hash(filepath)
                if stored_hash == current_hash:
                    print(f"  Already downloaded: {filename}")
                    return filepath
            
            # Download the file
            response = self.session.get(url, stream=True, timeout=30)
            response.raise_for_status()
            
            # Save file
            with open(filepath, 'wb') as f:
                for chunk in response.iter_content(chunk_size=8192):
                    f.write(chunk)
            
            # Calculate hash and store metadata
            file_hash = self.get_file_hash(filepath)
            file_size = filepath.stat().st_size
            
            self.metadata['documents'][url] = {
                'title': title,
                'author': author,
                'category': category,
                'description': description,
                'filename': filename,
                'filepath': str(filepath.relative_to(self.base_dir)),
                'hash': file_hash,
                'size': file_size,
                'downloaded': time.strftime('%Y-%m-%d %H:%M:%S'),
                'source': parsed_url.netloc
            }
            
            print(f"  Downloaded: {filename} ({file_size / 1024:.1f} KB)")
            return filepath
            
        except Exception as e:
            print(f"  Error downloading {title}: {e}")
            return None
    
    def download_essential_texts(self):
        """Download core essential Bahai texts"""
        
        # Note: These are sample documents. In practice, we'll look for available PDFs
        # from official Bahai websites and download them programmatically
        essential_documents = [
            # Sample PDFs from official sources
            {
                'url': 'https://www.bahai.org/library/',
                'title': 'The Kitab-i-Iqan',
                'author': 'Baha\'u\'llah',
                'category': 'Holy Text',
                'description': 'The Book of Certitude - fundamental theological work'
            },
            {
                'url': 'https://reference.bahai.org/en/t/b/KA/ka-1.html#pg1',
                'title': 'The Kitab-i-Aqdas',
                'author': 'Baha\'u\'llah', 
                'category': 'Holy Text',
                'description': 'The Most Holy Book - the central book of Baha\'i law'
            },
            {
                'url': 'https://reference.bahai.org/en/t/b/GWB/gwb-1.html#pg1',
                'title': 'Gleanings from the Writings of Baha\'u\'llah',
                'author': 'Baha\'u\'llah',
                'category': 'Holy Text', 
                'description': 'Selections from Baha\'u\'llah\'s writings'
            },
            {
                'url': 'https://reference.bahai.org/en/t/b/HW/',
                'title': 'The Hidden Words',
                'author': 'Baha\'u\'llah',
                'category': 'Holy Text',
                'description': 'Mystical and spiritual teachings in brief form'
            },
            
            # Abdul-Baha - Core Writings  
            {
                'url': 'https://reference.bahai.org/en/t/ab/SAQ/',
                'title': 'Some Answered Questions', 
                'author': 'Abdul-Baha',
                'category': 'Holy Text',
                'description': 'Authoritative exposition of Baha\'i teachings'
            },
            {
                'url': 'https://reference.bahai.org/en/t/ab/PUP/',
                'title': 'The Promulgation of Universal Peace',
                'author': 'Abdul-Baha',
                'category': 'Holy Text', 
                'description': 'Talks given during Abdul-Baha\'s journey to America'
            },
            
            # Shoghi Effendi - Core Writings
            {
                'url': 'https://reference.bahai.org/en/t/se/GPB/',
                'title': 'God Passes By',
                'author': 'Shoghi Effendi',
                'category': 'Historical Text',
                'description': 'Authoritative history of the first Baha\'i century'
            },
            {
                'url': 'https://reference.bahai.org/en/t/se/WOB/', 
                'title': 'The World Order of Baha\'u\'llah',
                'author': 'Shoghi Effendi',
                'category': 'Administrative Text',
                'description': 'Letters on the World Order of Baha\'u\'llah'
            },
            
            # Universal House of Justice - Key Messages
            {
                'url': 'https://universalhouseofjustice.bahai.org/sites/default/files/2022-09/The%20Constitution%20of%20the%20Universal%20House%20of%20Justice.pdf',
                'title': 'The Constitution of the Universal House of Justice',
                'author': 'Universal House of Justice',
                'category': 'Administrative Text',
                'description': 'Foundational administrative document'
            },
            
            # Compilations
            {
                'url': 'https://reference.bahai.org/en/t/c/BP/',
                'title': 'Baha\'i Prayers',
                'author': 'Compilation',
                'category': 'Prayer Book',
                'description': 'Collection of Baha\'i prayers and devotional writings'
            }
        ]
        
        print(f"Downloading {len(essential_documents)} essential Baha'i texts...")
        downloaded_count = 0
        
        for doc in essential_documents:
            if self.download_document(**doc):
                downloaded_count += 1
            time.sleep(1)  # Be respectful to servers
        
        print(f"\nDownload Summary:")
        print(f"  Successfully downloaded: {downloaded_count}/{len(essential_documents)} documents")
        print(f"  Files stored in: {self.confirmed_dir}")
        
        self.save_metadata()
        return downloaded_count
    
    def generate_download_report(self):
        """Generate a report of downloaded documents"""
        if not self.metadata['documents']:
            print("No documents downloaded yet.")
            return
        
        report_file = self.base_dir / "download_report.txt"
        with open(report_file, 'w', encoding='utf-8') as f:
            f.write("BAHAI RESOURCE LIBRARY - DOCUMENT DOWNLOAD REPORT\n")
            f.write("=" * 60 + "\n\n")
            f.write(f"Generated: {time.strftime('%Y-%m-%d %H:%M:%S')}\n")
            f.write(f"Total Documents: {len(self.metadata['documents'])}\n\n")
            
            # Group by author
            by_author = {}
            for url, doc in self.metadata['documents'].items():
                author = doc['author']
                if author not in by_author:
                    by_author[author] = []
                by_author[author].append(doc)
            
            for author, docs in by_author.items():
                f.write(f"{author.upper()}\n")
                f.write("-" * len(author) + "\n")
                for doc in sorted(docs, key=lambda x: x['title']):
                    f.write(f"  • {doc['title']}\n")
                    f.write(f"    Category: {doc['category']}\n")
                    f.write(f"    File: {doc['filename']} ({doc['size']/1024:.1f} KB)\n")
                    f.write(f"    Downloaded: {doc['downloaded']}\n\n")
                f.write("\n")
        
        print(f"Download report saved: {report_file}")

def main():
    """Main function to run the document downloader"""
    print("Bahai Document Downloader v0.2.0")
    print("=" * 40)
    
    downloader = BahaiDocumentDownloader()
    
    # Download essential texts
    downloader.download_essential_texts()
    
    # Generate report
    downloader.generate_download_report()
    
    print("\nDocument download process complete!")
    print("Next steps:")
    print("1. Review downloaded documents in the 'documents' folder")
    print("2. Check document_metadata.json for full catalog")
    print("3. Run the PDF text extraction process")

if __name__ == "__main__":
    main()