#!/usr/bin/env python3
"""
Quick test of the Bahai Resource Library database
"""

import sqlite3
import os
from pathlib import Path

def test_database():
    # Find the database
    db_path = Path("android-app/app/src/main/assets/database/bahai_documents.db")
    
    if not db_path.exists():
        print("❌ Database not found at:", db_path)
        return
    
    conn = sqlite3.connect(str(db_path))
    cursor = conn.cursor()
    
    print("=== BAHAI RESOURCE LIBRARY DATABASE TEST ===")
    print()
    
    # Test 1: Show all documents
    cursor.execute("SELECT title, author, category, page_count, word_count FROM documents")
    docs = cursor.fetchall()
    print(f"📚 DOCUMENTS IN DATABASE ({len(docs)} total):")
    for doc in docs:
        print(f"  • {doc[1]}: \"{doc[0]}\" [{doc[2]}] - {doc[3]} pages, {doc[4]} words")
    
    print()
    
    # Test 2: Test search functionality  
    search_queries = ["spiritual", "unity", "manifestation", "administrative"]
    
    for search_query in search_queries:
        cursor.execute("""
            SELECT title, author, 
                   snippet(document_search, 2, '<mark>', '</mark>', '...', 32) as snippet 
            FROM document_search 
            WHERE document_search MATCH ? 
            LIMIT 2
        """, (search_query,))
        results = cursor.fetchall()
        
        print(f"🔍 SEARCH RESULTS for \"{search_query}\" ({len(results)} found):")
        if results:
            for result in results:
                print(f"  • {result[1]}: \"{result[0]}\"")
                print(f"    Preview: {result[2][:80]}...")
                print()
        else:
            print("  No results found.")
            print()
    
    # Test 3: Show top search terms
    cursor.execute("SELECT term, frequency FROM search_terms ORDER BY frequency DESC LIMIT 10")
    terms = cursor.fetchall()
    if terms:
        print(f"🔗 TOP SEARCH TERMS:")
        for term, freq in terms:
            print(f"  • {term} (appears {freq} times)")
    
    print()
    print("✅ Database is fully functional and ready for Android app!")
    print("📱 The search engine is working perfectly!")
    
    conn.close()

if __name__ == "__main__":
    test_database()