package com.epam.tishkin;

import com.epam.tishkin.authorization.exception.AuthorDoesNotExistException;
import com.epam.tishkin.authorization.exception.BookDoesNotExistException;
import com.epam.tishkin.library.Author;
import com.epam.tishkin.library.Book;
import com.epam.tishkin.library.Library;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LibraryTest {
    private Library library;

    @BeforeEach
    public void setUp() {
        library = new Library();
        Author firstAuthor = new Author("Pushkin");
        Author secondAuthor = new Author("Lermontov");
        Book firstBook = new Book("Ruslan and Lyudmila", "Pushkin", 1458963258796L,
                1820, 325);
        Book secondBook = new Book("Borodino", "Lermontov", 1336985478569L,
                1937, 86);
        firstAuthor.getBooks().add(firstBook);
        secondAuthor.getBooks().add(secondBook);
        library.getAuthors().add(firstAuthor);
        library.getAuthors().add(secondAuthor);
    }

    @Test
    public void testAddBook() {
        Book book = new Book("A letter to a woman", "Esenin" ,1478541231111L, 1924, 215);
        boolean status = library.addBook(book);
        Assertions.assertTrue(status);
        int expectedLibrarySize = 3;
        int actualLibrarySize = library.getAuthors().size();
        Assertions.assertEquals(expectedLibrarySize, actualLibrarySize);
        Author expectedAuthor = new Author("Esenin");
        Assertions.assertTrue(library.getAuthors().contains(expectedAuthor));
    }

    @Test
    public void testDeleteBook() {
        boolean status = library.deleteBook("Ruslan and Lyudmila", "Pushkin");
        Assertions.assertTrue(status);
        status = library.deleteBook("Ruslan and Lyudmila", "Pushkin");
        Assertions.assertFalse(status);
    }

    @Test
    public void testAddAuthor() {
        boolean status = library.addAuthor("Pushkin");
        Assertions.assertFalse(status);
        status = library.addAuthor("Bunin");
        Assertions.assertTrue(status);
        int expectedLibrarySize = 3;
        int actualLibrarySize = library.getAuthors().size();
        Assertions.assertEquals(expectedLibrarySize, actualLibrarySize);
        Author expectedAuthor = new Author("Bunin");
        Assertions.assertTrue(library.getAuthors().contains(expectedAuthor));
    }

    @Test
    public void testDeleteAuthor() {
        boolean status = library.deleteAuthor("Pushkin");
        Assertions.assertTrue(status);
        int expectedLibrarySize = 1;
        int actualLibrarySize = library.getAuthors().size();
        Assertions.assertEquals(expectedLibrarySize, actualLibrarySize);
        Author author = new Author("Lermontov");
        Assertions.assertTrue(library.getAuthors().contains(author));
    }

    @Test
    public void testSearchBookForTitle() throws BookDoesNotExistException {
        Book expectedBook = new Book("Borodino", "Lermontov",
                1336985478569L, 1937, 86);
        Book actualBook = library.searchBookForTitle("Bor");
        Assertions.assertEquals(expectedBook, actualBook);
        Assertions.assertThrows(BookDoesNotExistException.class, () -> library.searchBookForTitle("NotExist"));
    }

    @Test
    public void testAddAndDeleteBookmark() throws BookDoesNotExistException {
        int page = 15;
        Book expectedBook = library.addBookmark("Bor", page);
        Assertions.assertTrue(expectedBook.getBookmark().getMark());
        Assertions.assertEquals(page, expectedBook.getBookmark().getPage());
        Assertions.assertThrows(BookDoesNotExistException.class, () -> library.addBookmark("NotExist", page));
        expectedBook = library.deleteBookmark("Bor");
        Assertions.assertFalse(expectedBook.getBookmark().getMark());
    }

    @Test
    public void testSearchBooksForAuthor() throws AuthorDoesNotExistException {
        Author expectedAuthor = new Author("Pushkin");
        Author actualAuthor = library.searchBooksForAuthor("Pus");
        Assertions.assertEquals(expectedAuthor, actualAuthor);
        Assertions.assertThrows(AuthorDoesNotExistException.class, () -> library.searchBooksForAuthor("NotExist"));
    }
}
