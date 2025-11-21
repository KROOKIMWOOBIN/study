package javacore.intermediate.quiz;

public class Inner {
    public static void main(String[] args) {
        Library library = new Library(4);
        library.addBook("책1", "저자1");
        library.addBook("책2", "저자2");
        library.addBook("책3", "저자3");
        library.addBook("책4", "저자4");
        library.addBook("책5", "저자5");
        library.showBooks();
    }
}
class Library {

    private final Book[] books;
    private int bookSize;

    Library(int maxBook) {
        this.books = new Book[maxBook];
    }

    public void addBook(String title, String author) {
        if(bookSize >= books.length) {
            System.out.println("도서관 저장 공간이 부족합니다.");
            return;
        }
        Book book = new Book(title, author);
        books[bookSize++] = book;
    }

    public void showBooks() {
        System.out.println("== 책 목록 출력 ==");
        for(int i = 0; i < bookSize; i++) {
            System.out.println("도서 제목: " + books[i].title + ", 저자: " + books[i].author);
        }
    }

    private static class Book {
        private final String title;
        private final String author;
        Book(String title, String author) {
            this.title = title;
            this.author = author;
        }
    }
}
