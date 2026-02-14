#!/usr/bin/env python3
"""
Simple Bahai Document Finder and Downloader
Searches for publicly available Bahai documents and downloads them
"""

import requests
import os
import json
from pathlib import Path
import time
import re

class SimpleBahaiDownloader:
    def __init__(self, base_dir="documents"):
        self.base_dir = Path(base_dir)
        self.confirmed_dir = self.base_dir / "confirmed-official"
        self.pending_dir = self.base_dir / "pending-permissions"
        
        # Create directories
        self.confirmed_dir.mkdir(parents=True, exist_ok=True)
        self.pending_dir.mkdir(parents=True, exist_ok=True)
        
        # Session for requests
        self.session = requests.Session()
        self.session.headers.update({
            'User-Agent': 'Bahai Resource Library Document Downloader 0.2.0'
        })
        
        # Metadata storage
        self.metadata = {"documents": {}, "last_updated": None}

    def create_sample_documents(self):
        """Create sample text files representing Bahai documents for testing"""
        
        sample_docs = [
            {
                'filename': 'Bahaullah_Kitab-i-Iqan_Sample.txt',
                'title': 'The Kitab-i-Iqan (Sample)',
                'author': 'Bahá\'u\'lláh',
                'category': 'Holy Text',
                'content': '''The Kitab-i-Iqan (The Book of Certitude)

By Bahá'u'lláh

This is a sample representation of one of the most important works of Bahá'u'lláh, 
the Founder of the Bahá'í Faith. The Kitab-i-Iqan was revealed in Baghdad around 
1862 and addresses the nature of revelation and the station of the Prophets of God.

Key themes in the Kitab-i-Iqan include:

- The nature of progressive revelation
- The unity of all the Messengers of God  
- The spiritual interpretation of scripture
- The concept of the Return
- Tests and trials as means of spiritual growth

"The essence of these words is this: they that tread the path of faith, they that 
thirst for the wine of certitude, must cleanse themselves of all that is earthly—
their ears from idle talk, their minds from vain imaginings, their hearts from 
worldly affections, their eyes from that which perisheth."

This work demonstrates Bahá'u'lláh's profound knowledge of Islamic theology while 
introducing new spiritual concepts that would become central to the Bahá'í Faith.

Note: This is a sample text for app development purposes. The full text is 
available through official Bahá'í sources at bahai.org and reference.bahai.org.'''
            },
            {
                'filename': 'Abdul-Baha_Some_Answered_Questions_Sample.txt',
                'title': 'Some Answered Questions (Sample)',
                'author': '\'Abdu\'l-Bahá',
                'category': 'Holy Text',
                'content': '''Some Answered Questions

By 'Abdu'l-Bahá

This is a sample representation of 'Abdu'l-Bahá's authoritative exposition 
of Bahá'í teachings, compiled from talks given to Laura Clifford Barney 
in 'Akká between 1904-1906.

Topics covered include:

- The nature of the soul and spiritual existence
- The relationship between science and religion  
- Biblical and Quranic interpretation
- Social and economic questions
- The oneness of humanity

Sample question and answer:

Question: What is the nature of the rational soul?

Answer: "The rational soul—that is to say, the human spirit—has neither 
entered this body nor existed through it; so after the disintegration of 
the composition of the body, how should it be in need of a substance through 
which it may exist? On the contrary, the rational soul is the substance 
through which the body exists."

This work serves as one of the most important doctrinal texts of the Bahá'í 
Faith, providing authoritative interpretations of spiritual and social questions.

Note: This is a sample text for app development purposes.'''
            },
            {
                'filename': 'Shoghi_Effendi_World_Order_Sample.txt',
                'title': 'The World Order of Bahá\'u\'lláh (Sample)',
                'author': 'Shoghi Effendi',
                'category': 'Administrative Text',
                'content': '''The World Order of Bahá'u'lláh

By Shoghi Effendi, Guardian of the Bahá'í Faith

This is a sample from a collection of letters written by Shoghi Effendi 
between 1929-1936, explaining the administrative principles of the Bahá'í Faith.

Key concepts include:

- The Administrative Order as embryonic World Order
- The relationship between spiritual and administrative aspects
- The role of elected institutions
- The concept of collective decision-making

Sample excerpt:

"The Administrative Order, which ever since 'Abdu'l-Bahá's ascension has 
evolved and is taking shape under our very eyes in no fewer than forty 
countries of the world, may be considered as the framework of that 
wondrous System which the Revealer of the Bahá'í Faith has conceived for 
the unification and administration of a world commonwealth."

"This Administrative Order is fundamentally different from anything that any 
Prophet has previously established, inasmuch as Bahá'u'lláh has Himself 
revealed its principles, established its institutions, appointed the person 
to interpret His Word and conferred the necessary authority on the body 
designed to supplement and apply His legislative ordinances."

Note: This is a sample text for app development purposes.'''
            }
        ]
        
        print("Creating sample Bahá'í documents for testing...")
        for doc in sample_docs:
            file_path = self.confirmed_dir / doc['filename']
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(doc['content'])
            
            # Store metadata
            self.metadata['documents'][str(file_path)] = {
                'title': doc['title'],
                'author': doc['author'], 
                'category': doc['category'],
                'filename': doc['filename'],
                'filepath': str(file_path.relative_to(self.base_dir)),
                'size': file_path.stat().st_size,
                'source': 'Sample for development',
                'downloaded': time.strftime('%Y-%m-%d %H:%M:%S')
            }
            
            print(f"  Created: {doc['filename']}")
        
        # Save metadata
        metadata_file = self.base_dir / "document_metadata.json"
        with open(metadata_file, 'w', encoding='utf-8') as f:
            json.dump(self.metadata, f, indent=2, ensure_ascii=False)
        
        print(f"\nSample documents created successfully!")
        print(f"Metadata saved to: {metadata_file}")
        return len(sample_docs)

def main():
    """Main function"""
    print("Simple Bahai Document Creator v0.2.0")
    print("=" * 40)
    
    downloader = SimpleBahaiDownloader()
    downloader.create_sample_documents()
    
    print("\nNext steps:")
    print("1. Run the document processor to extract and index text")
    print("2. Test search functionality with sample documents")
    print("3. Replace with real documents from official sources")

if __name__ == "__main__":
    main()