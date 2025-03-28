import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './Dashboard.css';

const Dashboard = () => {
  const [books, setBooks] = useState([]);
  const [booksLent, setBooksLent] = useState([]);

    const [newBook, setNewBook] = useState({
    name: '',
    author: '',
    totalBooks: 0,
    availableBooks: 0
  });
  const [editingBookId, setEditingBookId] = useState(null);
  const [showAddBookForm, setShowAddBookForm] = useState(false); // Add state for toggling form visibility



  const handleAddBooksLent = async (lbookId) => {
    try {
      const newBooksLentData = {
        userId: localStorage.getItem("userId"),
        bookId: lbookId
      };
      await axios.post('http://localhost:8083/api/lend', newBooksLentData, {
        headers: {
          'Authorization': localStorage.getItem('jwtToken'),
          'Content-Type': 'application/json'
        }
      });
      fetchData(); 
    } catch (error) {
      console.error('Error adding books lent:', error);
    }
  };

  const fetchData = async () => {
    try {
      const booksResponse = await axios.get('http://localhost:8082/api/book', {
        headers: {
          'Authorization': localStorage.getItem('jwtToken'),
          'Content-Type': 'application/json'
        }
      });
      setBooks(booksResponse.data);

      const booksLentResponse = await axios.get('http://localhost:8083/api/lend', {
        headers: {
          'Authorization': localStorage.getItem('jwtToken'),
          'Content-Type': 'application/json'
        }
      });
      setBooksLent(booksLentResponse.data);
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleAddBook = async () => {
    try {
      await axios.post('http://localhost:8082/api/book', newBook, {
        headers: {
          'Authorization': localStorage.getItem('jwtToken'),
          'Content-Type': 'application/json'
        }
      });
      setNewBook({
        name: '',
        author: '',
        totalBooks: 0,
        availableBooks: 0
      });
      fetchData();
    } catch (error) {
      console.error('Error adding book:', error);
    }
  };

  const handleEditBook = async (bookId) => {
    try {
      const updatedBook = { ...newBook, bookId };
      await axios.put(`http://localhost:8082/api/book`, updatedBook, {
        headers: {
          'Authorization': localStorage.getItem('jwtToken'),
          'Content-Type': 'application/json'
        }
      });
      setEditingBookId(null);
      setShowAddBookForm(false);
      setNewBook({
        name: '',
        author: '',
        totalBooks: 0,
        availableBooks: 0
      });
      fetchData();
    } catch (error) {
      console.error('Error editing book:', error);
    }
  };

  const handleDeleteBook = async (bookId) => {
    try {
      await axios.delete(`http://localhost:8082/api/book/${bookId}`, {
        headers: {
          'Authorization': localStorage.getItem('jwtToken')
        }
      });
      fetchData();
    } catch (error) {
      console.error('Error deleting book:', error);
    }
  };

  const handleDeleteBooksLent = async (lentId) => {
    try {
      await axios.delete(`http://localhost:8083/api/lend/${lentId}`, {
        headers: {
          'Authorization': localStorage.getItem('jwtToken')
        }
      });
      fetchData();
    } catch (error) {
      console.error('Error deleting books lent:', error);
    }
  };

  const sortedBooks = [...books].sort((a, b) => a.bookId - b.bookId);
  const sortedBooksLent = [...booksLent].sort((a, b) => a.bookId - b.bookId);

  return (
    <div className='dashboard-container'>
      <h1>
        Library Management System
      </h1>
      <div className='section'>
        <h2>
          My Books
        </h2>
        <div className='section'>
        </div>
        <table className="table">
          <thead>
            <tr>
              <th>Lent ID</th>
              <th>User ID</th>
              <th>Book ID</th>
              <th>Delete</th>
            </tr>
          </thead>
          <tbody>
            {sortedBooksLent.map(bookLent => (
              <tr key={bookLent.lentId}>
                <td>{bookLent.lentId}</td>
                <td>{bookLent.userId}</td>
                <td>{bookLent.bookId}</td>
                <td>
                  <button onClick={() => handleDeleteBooksLent(bookLent.lentId)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <div className='section'>
        <h2>
          Avalible Books
        </h2>
        <div>
        <button onClick={() => setShowAddBookForm(!showAddBookForm)}>Add Book</button>
        {showAddBookForm && (
          <div className='form'>
            <input type="text" value={newBook.name} onChange={(e) => setNewBook({ ...newBook, name: e.target.value })} placeholder="Name" />
            <input type="text" value={newBook.author} onChange={(e) => setNewBook({ ...newBook, author: e.target.value })} placeholder="Author" />
            <input type="number" value={newBook.totalBooks} onChange={(e) => setNewBook({ ...newBook, totalBooks: e.target.value })} placeholder="Total Books" />
            <input type="number" value={newBook.availableBooks} onChange={(e) => setNewBook({ ...newBook, availableBooks: e.target.value })} placeholder="Available Books" />
            {editingBookId ? (
              <button onClick={() => handleEditBook(editingBookId)}>Save</button>
            ) : (
              <button onClick={handleAddBook}>Add Book</button>
            )}
          </div>
        )}
        </div>
        <table className="table">
          <thead>
            <tr>
              <th>Book ID</th>
              <th>Name</th>
              <th>Author</th>
              <th>Total Books</th>
              <th>Avaliable Books</th>
              {localStorage.getItem("userId") === "admin" && <th>Edit</th>}
              {localStorage.getItem("userId") === "admin" && <th>Delete</th>}
              {localStorage.getItem("userId") !== "admin" && <th>lend</th>}
            </tr>
          </thead>
          <tbody>
            {sortedBooks.map(book => (
              <tr key={book.bookId}>
                <td>{book.bookId}</td>
                <td>{book.name}</td>
                <td>{book.author}</td>
                <td>{book.totalBooks}</td>
                <td>{book.availableBooks}</td>
                {localStorage.getItem("userId") === "admin" && <td><button onClick={() => {
                        setEditingBookId(book.bookId);
                        setShowAddBookForm(true);
                        setNewBook({
                          name: book.name,
                          author: book.author,
                          totalBooks: book.totalBooks,
                          availableBooks: book.availableBooks
                        });
                      }}>Edit</button></td>}
                {localStorage.getItem("userId") === "admin" && <td><button onClick={() => handleDeleteBook(book.bookId)}>Delete</button></td>}
                {localStorage.getItem("userId") !== "admin" && <td><button onClick={() => handleAddBooksLent(book.bookId)}>Lend</button></td>}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Dashboard;
