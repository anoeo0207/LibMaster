// Sample book data
let books = [
    {
        id: 1,
        title: "To Kill a Mockingbird",
        author: "Harper Lee",
        category: "fiction",
        isbn: "9780061120084",
        publishedDate: "1960-07-11",
        status: "available",
        description: "The unforgettable novel of a childhood in a sleepy Southern town and the crisis of conscience that rocked it."
    },
    {
        id: 2,
        title: "1984",
        author: "George Orwell",
        category: "fiction",
        isbn: "9780451524935",
        publishedDate: "1949-06-08",
        status: "borrowed",
        description: "A dystopian novel set in a totalitarian society ruled by the Party, which has total control over every aspect of people's lives."
    },
    {
        id: 3,
        title: "A Brief History of Time",
        author: "Stephen Hawking",
        category: "science",
        isbn: "9780553380163",
        publishedDate: "1988-09-01",
        status: "available",
        description: "A landmark volume in science writing by one of the great minds of our time."
    },
    {
        id: 4,
        title: "The Guns of August",
        author: "Barbara W. Tuchman",
        category: "history",
        isbn: "9780345476098",
        publishedDate: "1962-10-24",
        status: "available",
        description: "A landmark history that brings the outbreak of World War I to vivid, dramatic life."
    },
    {
        id: 5,
        title: "Sapiens: A Brief History of Humankind",
        author: "Yuval Noah Harari",
        category: "non-fiction",
        isbn: "9780062316097",
        publishedDate: "2014-02-10",
        status: "borrowed",
        description: "A groundbreaking narrative of humanity's creation and evolution."
    }
];

// DOM Elements
const booksList = document.getElementById('books-list');
const bookForm = document.getElementById('book-form');
const searchInput = document.getElementById('search');
const searchBtn = document.getElementById('search-btn');
const categoryFilter = document.getElementById('category-filter');
const statusFilter = document.getElementById('status-filter');
const addBookBtn = document.getElementById('add-book-btn');
const cancelBtn = document.getElementById('cancel-btn');
const navItems = document.querySelectorAll('nav ul li');
const views = document.querySelectorAll('.view');

// Modal Elements
const modal = document.getElementById('book-modal');
const closeModal = document.querySelector('.close');
const modalTitle = document.getElementById('modal-title');
const modalAuthor = document.getElementById('modal-author');
const modalCategory = document.getElementById('modal-category');
const modalIsbn = document.getElementById('modal-isbn');
const modalPublished = document.getElementById('modal-published');
const modalStatus = document.getElementById('modal-status');
const modalDescription = document.getElementById('modal-description');
const editBookBtn = document.getElementById('edit-book-btn');
const deleteBookBtn = document.getElementById('delete-book-btn');
const toggleStatusBtn = document.getElementById('toggle-status-btn');

// Statistics Elements
const totalBooksEl = document.getElementById('total-books');
const availableBooksEl = document.getElementById('available-books');
const borrowedBooksEl = document.getElementById('borrowed-books');
const categoriesCountEl = document.getElementById('categories-count');
const categoryChart = document.getElementById('category-chart');

// Initialize the app
function init() {
    renderBooks();
    updateStatistics();
    renderCategoryChart();
}

// Render books to the DOM
function renderBooks(filteredBooks = books) {
    booksList.innerHTML = '';
    
    if (filteredBooks.length === 0) {
        booksList.innerHTML = '<p class="no-books">No books found. Try a different search or filter.</p>';
        return;
    }
    
    filteredBooks.forEach(book => {
        const bookCard = document.createElement('div');
        bookCard.className = 'book-card';
        bookCard.dataset.id = book.id;
        
        bookCard.innerHTML = `
            <h3 class="book-title">${book.title}</h3>
            <p class="book-author">by ${book.author}</p>
            <span class="book-category">${book.category}</span>
            <span class="book-status status-${book.status}">${book.status}</span>
        `;
        
        bookCard.addEventListener('click', () => openBookDetails(book));
        booksList.appendChild(bookCard);
    });
}

// Filter books based on search and filters
function filterBooks() {
    const searchTerm = searchInput.value.toLowerCase();
    const categoryValue = categoryFilter.value;
    const statusValue = statusFilter.value;
    
    const filteredBooks = books.filter(book => {
        const matchesSearch = book.title.toLowerCase().includes(searchTerm) || 
                             book.author.toLowerCase().includes(searchTerm) ||
                             book.isbn.includes(searchTerm);
        
        const matchesCategory = categoryValue === '' || book.category === categoryValue;
        const matchesStatus = statusValue === '' || book.status === statusValue;
        
        return matchesSearch && matchesCategory && matchesStatus;
    });
    
    renderBooks(filteredBooks);
}

// Open book details modal
function openBookDetails(book) {
    modalTitle.textContent = book.title;
    modalAuthor.textContent = book.author;
    modalCategory.textContent = book.category;
    modalIsbn.textContent = book.isbn;
    modalPublished.textContent = formatDate(book.publishedDate);
    modalStatus.textContent = book.status;
    modalDescription.textContent = book.description;
    
    // Set button text based on status
    toggleStatusBtn.textContent = book.status === 'available' ? 'Mark as Borrowed' : 'Mark as Available';
    toggleStatusBtn.className = book.status === 'available' ? 'btn primary' : 'btn success';
    
    // Store book ID in buttons for event handlers
    editBookBtn.dataset.id = book.id;
    deleteBookBtn.dataset.id = book.id;
    toggleStatusBtn.dataset.id = book.id;
    
    modal.style.display = 'block';
}

// Close the modal
function closeBookDetails() {
    modal.style.display = 'none';
}

// Add a new book
function addBook(e) {
    e.preventDefault();
    
    const newBook = {
        id: books.length > 0 ? Math.max(...books.map(book => book.id)) + 1 : 1,
        title: document.getElementById('title').value,
        author: document.getElementById('author').value,
        category: document.getElementById('category').value,
        isbn: document.getElementById('isbn').value,
        publishedDate: document.getElementById('published-date').value,
        status: 'available',
        description: document.getElementById('description').value
    };
    
    books.push(newBook);
    bookForm.reset();
    
    // Switch to books view
    switchView('books');
    renderBooks();
    updateStatistics();
    renderCategoryChart();
    
    // Show success message
    alert('Book added successfully!');
}

// Delete a book
function deleteBook(id) {
    if (confirm('Are you sure you want to delete this book?')) {
        books = books.filter(book => book.id !== parseInt(id));
        closeBookDetails();
        renderBooks();
        updateStatistics();
        renderCategoryChart();
    }
}

// Toggle book status (available/borrowed)
function toggleBookStatus(id) {
    const bookIndex = books.findIndex(book => book.id === parseInt(id));
    
    if (bookIndex !== -1) {
        books[bookIndex].status = books[bookIndex].status === 'available' ? 'borrowed' : 'available';
        closeBookDetails();
        renderBooks();
        updateStatistics();
    }
}

// Edit book (open form with book data)
function editBook(id) {
    const book = books.find(book => book.id === parseInt(id));
    
    if (book) {
        // Switch to add book view
        switchView('add-book');
        
        // Fill form with book data
        document.getElementById('title').value = book.title;
        document.getElementById('author').value = book.author;
        document.getElementById('category').value = book.category;
        document.getElementById('isbn').value = book.isbn;
        document.getElementById('published-date').value = book.publishedDate;
        document.getElementById('description').value = book.description;
        
        // Change form submit behavior to update instead of add
        bookForm.dataset.mode = 'edit';
        bookForm.dataset.id = book.id;
        
        // Close the modal
        closeBookDetails();
    }
}

// Update statistics
function updateStatistics() {
    const totalBooks = books.length;
    const availableBooks = books.filter(book => book.status === 'available').length;
    const borrowedBooks = books.filter(book => book.status === 'borrowed').length;
    
    // Get unique categories
    const categories = [...new Set(books.map(book => book.category))];
    
    totalBooksEl.textContent = totalBooks;
    availableBooksEl.textContent = availableBooks;
    borrowedBooksEl.textContent = borrowedBooks;
    categoriesCountEl.textContent = categories.length;
}

// Render category chart
function renderCategoryChart() {
    categoryChart.innerHTML = '';
    
    // Count books by category
    const categoryCounts = {};
    books.forEach(book => {
        categoryCounts[book.category] = (categoryCounts[book.category] || 0) + 1;
    });
    
    // Find the maximum count for scaling
    const maxCount = Math.max(...Object.values(categoryCounts), 1);
    
    // Create bars for each category
    Object.entries(categor);
    
    // Create bars for each category
    Object.entries(categoryCounts).forEach(([category, count]) => {
        const barHeight = (count / maxCount) * 250; // Scale height to max 250px
        
        const bar = document.createElement('div');
        bar.className = 'chart-bar';
        bar.style.height = `${barHeight}px`;
        
        const label = document.createElement('div');
        label.className = 'chart-bar-label';
        label.textContent = category;
        
        const value = document.createElement('div');
        value.className = 'chart-bar-value';
        value.textContent = count;
        
        bar.appendChild(value);
        bar.appendChild(label);
        categoryChart.appendChild(bar);
    });
}

// Format date for display
function formatDate(dateString) {
    const options = { year: 'numeric', month: 'long', day: 'numeric' };
    return new Date(dateString).toLocaleDateString(undefined, options);
}

// Switch between views
function switchView(viewId) {
    // Hide all views
    views.forEach(view => view.classList.remove('active'));
    
    // Show selected view
    document.getElementById(viewId).classList.add('active');
    
    // Update navigation
    navItems.forEach(item => {
        if (item.dataset.view === viewId) {
            item.classList.add('active');
        } else {
            item.classList.remove('active');
        }
    });
}

// Event Listeners
// Navigation
navItems.forEach(item => {
    item.addEventListener('click', () => {
        switchView(item.dataset.view);
    });
});

// Search and filters
searchBtn.addEventListener('click', filterBooks);
searchInput.addEventListener('keyup', filterBooks);
categoryFilter.addEventListener('change', filterBooks);
statusFilter.addEventListener('change', filterBooks);

// Add book button in header
addBookBtn.addEventListener('click', () => {
    bookForm.reset();
    bookForm.dataset.mode = 'add';
    delete bookForm.dataset.id;
    switchView('add-book');
});

// Cancel button in form
cancelBtn.addEventListener('click', () => {
    bookForm.reset();
    switchView('books');
});

// Book form submission
bookForm.addEventListener('submit', (e) => {
    e.preventDefault();
    
    if (bookForm.dataset.mode === 'edit') {
        // Update existing book
        const bookId = parseInt(bookForm.dataset.id);
        const bookIndex = books.findIndex(book => book.id === bookId);
        
        if (bookIndex !== -1) {
            books[bookIndex] = {
                ...books[bookIndex],
                title: document.getElementById('title').value,
                author: document.getElementById('author').value,
                category: document.getElementById('category').value,
                isbn: document.getElementById('isbn').value,
                publishedDate: document.getElementById('published-date').value,
                description: document.getElementById('description').value
            };
            
            alert('Book updated successfully!');
        }
    } else {
        // Add new book
        addBook(e);
    }
    
    bookForm.reset();
    switchView('books');
    renderBooks();
    updateStatistics();
    renderCategoryChart();
});

// Modal close button
closeModal.addEventListener('click', closeBookDetails);

// Close modal when clicking outside
window.addEventListener('click', (e) => {
    if (e.target === modal) {
        closeBookDetails();
    }
});

// Book action buttons
editBookBtn.addEventListener('click', () => {
    editBook(editBookBtn.dataset.id);
});

deleteBookBtn.addEventListener('click', () => {
    deleteBook(deleteBookBtn.dataset.id);
});

toggleStatusBtn.addEventListener('click', () => {
    toggleBookStatus(toggleStatusBtn.dataset.id);
});

// Theme switcher
document.getElementById('theme').addEventListener('change', (e) => {
    if (e.target.value === 'dark') {
        document.documentElement.style.setProperty('--bg-color', '#121212');
        document.documentElement.style.setProperty('--sidebar-bg', '#1e1e1e');
        document.documentElement.style.setProperty('--card-bg', '#1e1e1e');
        document.documentElement.style.setProperty('--text-color', '#e0e0e0');
        document.documentElement.style.setProperty('--text-light', '#a0a0a0');
        document.documentElement.style.setProperty('--border-color', '#333');
    } else {
        document.documentElement.style.setProperty('--bg-color', '#f8f9fa');
        document.documentElement.style.setProperty('--sidebar-bg', '#fff');
        document.documentElement.style.setProperty('--card-bg', '#fff');
        document.documentElement.style.setProperty('--text-color', '#333');
        document.documentElement.style.setProperty('--text-light', '#666');
        document.documentElement.style.setProperty('--border-color', '#e0e0e0');
    }
});

// Initialize the app
init();