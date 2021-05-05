CREATE TABLE User
(
Login nvarchar(50) PRIMARY KEY,
Password nvarchar(50) NOT NULL,
Role nvarchar(50) NOT NULL
);

CREATE TABLE Bookmark
(
Id integer PRIMARY KEY AUTO_INCREMENT,
Book_title nvarchar(300) NOT NULL,
Page_number integer NOT NULL,
User_login nvarchar,
FOREIGN KEY(User_login) REFERENCES User(Login) ON DELETE CASCADE
);


