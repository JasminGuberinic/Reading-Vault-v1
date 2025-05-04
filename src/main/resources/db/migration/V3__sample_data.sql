-- V3__sample_data.sql

-- Insert sample users
INSERT INTO users (
    email,
    password_hash,
    role,
    is_active,
    created_at,
    last_login_at,
    auth_provider,
    provider_id,
    display_name,
    image_url
) VALUES
    (
        'alice@example.com',
        'hashedpassword1',
        'USER',
        TRUE,
        CURRENT_TIMESTAMP(),
        CURRENT_TIMESTAMP(),
        'LOCAL',
        NULL,
        'Alice',
        NULL
    ),
    (
        'bob@example.com',
        'hashedpassword2',
        'USER',
        TRUE,
        CURRENT_TIMESTAMP(),
        NULL,
        'LOCAL',
        NULL,
        'Bob',
        NULL
    );

-- Assume Alice gets id=1, Bob gets id=2 (auto-increment)

-- Insert sample books with user_id
INSERT INTO books (
    user_id,
    title,
    author,
    isbn,
    year_published,
    total_pages,
    status,
    condition,
    location,
    date_acquired,
    current_page,
    rating,
    started_reading,
    last_read_at
) VALUES
    (
        1, -- Alice
        '1984',
        'George Orwell',
        '9780451524935',
        1949,
        328,
        'COMPLETED',
        'GOOD',
        'Police A, Shelf 1',
        CURRENT_DATE(),
        328,
        5,
        DATEADD('DAY', -30, CURRENT_TIMESTAMP()),
        DATEADD('DAY', -1, CURRENT_TIMESTAMP())
    ),
    (
        2, -- Bob
        'To Kill a Mockingbird',
        'Harper Lee',
        '9780060935467',
        1960,
        281,
        'IN_PROGRESS',
        'EXCELLENT',
        'Police B, Shelf 2',
        CURRENT_DATE(),
        156,
        NULL,
        CURRENT_TIMESTAMP(),
        CURRENT_TIMESTAMP()
    ),
    (
        1, -- Alice
        'The Great Gatsby',
        'F. Scott Fitzgerald',
        '9780743273565',
        1925,
        180,
        'NOT_STARTED',
        'NEW',
        'Police C, Shelf 1',
        CURRENT_DATE(),
        0,
        NULL,
        NULL,
        NULL
    );

-- Assume books get ids 1, 2, 3 (in order above)

-- Insert sample book notes
INSERT INTO book_notes (
    book_id,
    content,
    page,
    chapter,
    created_at,
    updated_at
) VALUES
    (1, 'Key themes of surveillance and control', 45, 'Chapter 3', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
    (1, 'Analysis of Newspeak', 102, 'Chapter 7', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
    (2, 'Character introduction: Scout Finch', 15, 'Chapter 1', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- Insert sample lending records
INSERT INTO book_lending (
    book_id,
    borrower_name,
    borrower_contact,
    lending_date,
    expected_return_date,
    actual_return_date,
    notes
) VALUES
    (
        2,
        'John Doe',
        'john@example.com',
        CURRENT_TIMESTAMP(),
        DATEADD('DAY', 14, CURRENT_TIMESTAMP()),
        NULL,
        'First time borrower'
    ),
    (
        1,
        'Jane Smith',
        'jane@example.com',
        DATEADD('DAY', -30, CURRENT_TIMESTAMP()),
        DATEADD('DAY', -16, CURRENT_TIMESTAMP()),
        DATEADD('DAY', -15, CURRENT_TIMESTAMP()),
        'Returned on time'
    );

-- Insert sample reading progress
INSERT INTO reading_progress (
    book_id,
    current_page,
    timestamp,
    minutes_read,
    notes
) VALUES
    (2, 50, DATEADD('DAY', -5, CURRENT_TIMESTAMP()), 30, 'Started reading'),
    (2, 156, CURRENT_TIMESTAMP(), 45, 'Getting more interesting'),
    (1, 328, DATEADD('DAY', -1, CURRENT_TIMESTAMP()), 60, 'Finished the book');