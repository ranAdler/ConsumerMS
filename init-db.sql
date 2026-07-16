-- Initialize Consumer Database
USE consumer_db;

-- Create messages table
CREATE TABLE IF NOT EXISTS messages (
    id INT PRIMARY KEY,
    msg VARCHAR(500),
    operation VARCHAR(50),
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_operation (operation),
    INDEX idx_status (status),
    INDEX idx_last_updated (last_updated),
    INDEX idx_status_operation (status, operation)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create audit log table
CREATE TABLE IF NOT EXISTS audit_log (
    event_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id INT,
    operation VARCHAR(50),
    old_value VARCHAR(500),
    new_value VARCHAR(500),
    event_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    consumer_service VARCHAR(100) DEFAULT 'consumer-ms',
    INDEX idx_message_id (message_id),
    INDEX idx_operation (operation),
    INDEX idx_event_timestamp (event_timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Verify tables created
SELECT 'Database initialization completed successfully' AS status;