DROP TABLE Bookmark;

CREATE TABLE Bookmark
(
Id integer PRIMARY KEY AUTO_INCREMENT,
Book_title nvarchar(300) NOT NULL,
Page_number integer NOT NULL,
User_login nvarchar,
FOREIGN KEY(User_login) REFERENCES User(Login) ON DELETE CASCADE,
FOREIGN KEY(Book_title) REFERENCES Book(Title) ON DELETE CASCADE
);