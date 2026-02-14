#!/usr/bin/env python3
"""
Text to PDF Converter for Bahai Documents
Converts text files to properly formatted PDF documents
"""

import os
import json
from pathlib import Path
from reportlab.pdfgen import canvas
from reportlab.lib.pagesizes import letter, A4
from reportlab.lib.units import inch
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, PageBreak
from reportlab.lib.colors import black, darkblue
from io import BytesIO
import textwrap
import re

class BahaiTextToPDFConverter:
    def __init__(self, input_dir: str = "documents/official", output_dir: str = "documents/official"):
        self.input_dir = Path(input_dir)
        self.output_dir = Path(output_dir)
        self.output_dir.mkdir(parents=True, exist_ok=True)
        
        # PDF styling
        self.styles = getSampleStyleSheet()
        
        # Custom styles for Bahai texts
        self.title_style = ParagraphStyle(
            'BahaiTitle',
            parent=self.styles['Title'],
            fontSize=18,
            spaceAfter=30,
            alignment=1,  # Center
            textColor=darkblue,
            fontName='Helvetica-Bold'
        )
        
        self.author_style = ParagraphStyle(
            'BahaiAuthor',
            parent=self.styles['Normal'],
            fontSize=14,
            spaceAfter=20,
            alignment=1,  # Center
            textColor=black,
            fontName='Helvetica-Oblique'
        )
        
        self.body_style = ParagraphStyle(
            'BahaiBody',
            parent=self.styles['Normal'],
            fontSize=11,
            leading=14,
            spaceAfter=12,
            alignment=0,  # Justify
            fontName='Times-Roman'
        )
        
        self.quote_style = ParagraphStyle(
            'BahaiQuote',
            parent=self.styles['Normal'],
            fontSize=10,
            leading=13,
            leftIndent=30,
            rightIndent=30,
            spaceAfter=12,
            alignment=0,
            fontName='Times-Italic'
        )
    
    def load_metadata(self, text_file: Path) -> dict:
        """Load metadata from JSON file"""
        metadata_file = text_file.with_suffix('.json')
        if metadata_file.exists():
            with open(metadata_file, 'r', encoding='utf-8') as f:
                return json.load(f)
        return {}
    
    def clean_and_format_text(self, text: str) -> list:
        """Clean and format text into structured paragraphs"""
        # Remove extra whitespace and normalize line endings
        text = re.sub(r'\r\n', '\n', text)
        text = re.sub(r'\r', '\n', text)
        text = re.sub(r'\n{3,}', '\n\n', text)
        
        paragraphs = []
        current_paragraph = ""
        
        for line in text.split('\n'):
            line = line.strip()
            
            if not line:  # Empty line
                if current_paragraph:
                    paragraphs.append(('paragraph', current_paragraph.strip()))
                    current_paragraph = ""
                continue
            
            # Check if this line looks like a heading
            if len(line) < 100 and (line.isupper() or line.count(' ') < 4):
                if current_paragraph:
                    paragraphs.append(('paragraph', current_paragraph.strip()))
                    current_paragraph = ""
                paragraphs.append(('heading', line))
            else:
                # Add to current paragraph
                if current_paragraph:
                    current_paragraph += " " + line
                else:
                    current_paragraph = line
        
        # Don't forget the last paragraph
        if current_paragraph:
            paragraphs.append(('paragraph', current_paragraph.strip()))
        
        return paragraphs
    
    def create_pdf_from_text(self, text_file: Path) -> Path:
        """Convert a text file to PDF"""
        print(f"Converting {text_file.name} to PDF...")
        
        # Load text content
        with open(text_file, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Load metadata
        metadata = self.load_metadata(text_file)
        
        # Create PDF file path
        pdf_file = self.output_dir / text_file.name.replace('.txt', '.pdf')
        
        # Create PDF document
        doc = SimpleDocTemplate(
            str(pdf_file),
            pagesize=A4,
            rightMargin=0.75*inch,
            leftMargin=0.75*inch,
            topMargin=1*inch,
            bottomMargin=0.75*inch
        )
        
        story = []
        
        # Add title page
        if metadata.get('title'):
            story.append(Paragraph(metadata['title'], self.title_style))
            story.append(Spacer(1, 0.2*inch))
        
        if metadata.get('author'):
            story.append(Paragraph(f"by {metadata['author']}", self.author_style))
            story.append(Spacer(1, 0.3*inch))
        
        if metadata.get('description'):
            story.append(Paragraph(metadata['description'], self.body_style))
            story.append(Spacer(1, 0.2*inch))
        
        # Add a page break after title page
        story.append(PageBreak())
        
        # Process the main content
        paragraphs = self.clean_and_format_text(content)
        
        for para_type, para_text in paragraphs:
            if para_type == 'heading':
                heading_style = ParagraphStyle(
                    'BahaiHeading',
                    parent=self.styles['Heading2'],
                    fontSize=13,
                    spaceAfter=15,
                    spaceBefore=20,
                    alignment=0,
                    textColor=darkblue,
                    fontName='Helvetica-Bold'
                )
                story.append(Paragraph(para_text, heading_style))
            elif para_type == 'paragraph':
                # Check if it looks like a quote (indented or starts with quotation)
                if para_text.startswith('"') or len(para_text) > 200:
                    story.append(Paragraph(para_text, self.body_style))
                else:
                    story.append(Paragraph(para_text, self.body_style))
            
            story.append(Spacer(1, 0.1*inch))
        
        # Build the PDF
        doc.build(story)
        
        print(f"Created PDF: {pdf_file.name}")
        return pdf_file
    
    def convert_all_text_files(self):
        """Convert all text files to PDFs"""
        text_files = list(self.input_dir.glob('*.txt'))
        
        if not text_files:
            print("No text files found to convert")
            return []
        
        print(f"Found {len(text_files)} text files to convert...")
        
        pdf_files = []
        for text_file in text_files:
            try:
                pdf_file = self.create_pdf_from_text(text_file)
                pdf_files.append(pdf_file)
            except Exception as e:
                print(f"Error converting {text_file.name}: {e}")
        
        print(f"\nConversion complete! Created {len(pdf_files)} PDF files.")
        return pdf_files

def main():
    """Main function"""
    converter = BahaiTextToPDFConverter()
    
    print("Bahai Text to PDF Converter")
    print("=" * 40)
    print("Converting text files to properly formatted PDFs...\n")
    
    pdf_files = converter.convert_all_text_files()
    
    if pdf_files:
        print("\nCreated PDF files:")
        for pdf_file in pdf_files:
            print(f"  - {pdf_file.name}")
    
    return pdf_files

if __name__ == "__main__":
    main()