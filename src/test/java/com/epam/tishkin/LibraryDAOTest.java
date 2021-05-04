/**
package com.epam.tishkin;

import com.epam.tishkin.dao.LibraryDAO;
import com.epam.tishkin.exception.AuthorDoesNotExistException;
import com.epam.tishkin.exception.BookDoesNotExistException;
import com.epam.tishkin.models.User;
import com.epam.tishkin.client.Visitor;
import com.epam.tishkin.models.Author;
import com.epam.tishkin.models.Book;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class LibraryDAOTest {
    private LibraryDAO libraryDAO;
    private Visitor visitor;

    @BeforeEach
    public void setUp() {
        libraryDAO = new LibraryDAO();
        visitor = new User("Maxim");
        Author firstAuthor = new Author("Pushkin");
        Author secondAuthor = new Author("Lermontov");
        Book firstBook = new Book("Ruslan and Lyudmila", "Pushkin", 1458963258796L,
                1820, 325);
        Book secondBook = new Book("Borodino", "Lermontov", 1336985478569L,
                1937, 86);
        firstAuthor.getBooks().add(firstBook);
        secondAuthor.getBooks().add(secondBook);
        libraryDAO.getAuthors().add(firstAuthor);
        libraryDAO.getAuthors().add(secondAuthor);
    }

    @Test
    public void testAddBook() {
        Book book = new Book("A letter to a woman", "Esenin" ,1478541231111L, 1924, 215);
        boolean status = libraryDAO.addBook(book);
        Assertions.assertTrue(status);
        int expectedLibrarySize = 3;
        int actualLibrarySize = libraryDAO.getAuthors().size();
        Assertions.assertEquals(expectedLibrarySize, actualLibrarySize);
        Author expectedAuthor = new Author("Esenin");
        Assertions.assertTrue(libraryDAO.getAuthors().contains(expectedAuthor));
    }

    @Test
    public void testDeleteBook() {
        boolean status = libraryDAO.deleteBook("Ruslan and Lyudmila", "Pushkin");
        Assertions.assertTrue(status);
        status = libraryDAO.deleteBook("Ruslan and Lyudmila", "Pushkin");
        Assertions.assertFalse(status);
    }

    @Test
    public void testAddAuthor() {
        boolean status = libraryDAO.addAuthor("Pushkin");
        Assertions.assertFalse(status);
        status = libraryDAO.addAuthor("Bunin");
        Assertions.assertTrue(status);
        int expectedLibrarySize = 3;
        int actualLibrarySize = libraryDAO.getAuthors().size();
        Assertions.assertEquals(expectedLibrarySize, actualLibrarySize);
        Author expectedAuthor = new Author("Bunin");
        Assertions.assertTrue(libraryDAO.getAuthors().contains(expectedAuthor));
    }

    @Test
    public void testDeleteAuthor() {
        boolean status = libraryDAO.deleteAuthor("Pushkin");
        Assertions.assertTrue(status);
        int expectedLibrarySize = 1;
        int actualLibrarySize = libraryDAO.getAuthors().size();
        Assertions.assertEquals(expectedLibrarySize, actualLibrarySize);
        Author author = new Author("Lermontov");
        Assertions.assertTrue(libraryDAO.getAuthors().contains(author));
    }

    @Test
    public void testSearchBookForTitle() throws BookDoesNotExistException {
        Book expectedBook = new Book("Borodino", "Lermontov",
                1336985478569L, 1937, 86);
        Book actualBook = libraryDAO.searchBookForTitle("Bor");
        Assertions.assertEquals(expectedBook, actualBook);
        Assertions.assertThrows(BookDoesNotExistException.class, () -> libraryDAO.searchBookForTitle("NotExist"));
    }

    @Test
    public void testAddAndDeleteBookmark() {
        int page = 15;
        visitor.addBookmark("Borodino", page);
        int expectedBookmarkCount = 1;
        Assertions.assertEquals(expectedBookmarkCount, visitor.getMyBookmarks().size());
        visitor.addBookmark("Ruslan and Lyudmila", page);
        expectedBookmarkCount = 2;
        Assertions.assertEquals(expectedBookmarkCount, visitor.getMyBookmarks().size());
        visitor.deleteBookmark("Ruslan and Lyudmila");
        expectedBookmarkCount = 1;
        Assertions.assertEquals(expectedBookmarkCount, visitor.getMyBookmarks().size());
    }

    @Test
    public void testSearchBooksForAuthor() throws AuthorDoesNotExistException {
        Author expectedAuthor = new Author("Pushkin");
        Author actualAuthor = libraryDAO.searchBooksForAuthor("Pus");
        Assertions.assertEquals(expectedAuthor, actualAuthor);
        Assertions.assertThrows(AuthorDoesNotExistException.class, () -> libraryDAO.searchBooksForAuthor("NotExist"));
    }
}
 */