package com.epam.tishkin.dao;

import com.epam.tishkin.models.Book;

public interface AbstractLibraryDAO {

    boolean addBook(Book book, String authorName);
    boolean deleteBook(String title, String authorName);
    boolean addAuthor(String authorName);
    boolean deleteAuthor(String authorName);
    int addBooksFromCSV(String fileName);
    int addBooksFromJSON(String fileName);
    void searchBookForTitle(String title);
    void searchBooksForAuthor(String authorName);
    boolean searchBookForISBN(String ISBNumber);
    void searchBooksByYearRange(int startYear, int finishYear);
    void searchBookByYearPagesNumberAndTitle(int year, int pages, String title);
    Book findBookByFullTitle(String title);
    void closeSessionFactory();
}
