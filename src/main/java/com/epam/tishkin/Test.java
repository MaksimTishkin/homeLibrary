package com.epam.tishkin;

import com.epam.tishkin.models.Author;
import com.epam.tishkin.models.Book;
import com.epam.tishkin.server.dao.LibraryDAO;
import com.epam.tishkin.server.dao.impl.LibraryDAOImpl;
import com.epam.tishkin.server.rs.service.LibraryService;
import com.epam.tishkin.server.rs.service.impl.LibraryServiceImpl;

import java.util.List;

public class Test {
    private LibraryDAO libraryDAO = new LibraryDAOImpl();
    private LibraryService libraryService = new LibraryServiceImpl();

    public static void main(String[] args) {
        Author author = new Author("Anton");
        System.out.println(author.getBook());

        List<Book> books = new Test().libraryService.searchBookForTitle("t");
        Book currentBook = books.get(0);
        System.out.println(currentBook.getAuthor().getBook());
    }
}
