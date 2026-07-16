-- Create messages table if not exists
CREATE TABLE IF NOT EXISTS messages (
    id INT PRIMARY KEY,
    msg VARCHAR(500),
    operation VARCHAR(50),
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create audit log table (optional but recommended)
CREATE TABLE IF NOT EXISTS audit_log (
    event_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    message_id INT,
    operation VARCHAR(50),
    old_value VARCHAR(500),
    new_value VARCHAR(500),
    event_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    consumer_service VARCHAR(100) DEFAULT 'consumer-ms',
    INDEX idx_message_id (message_id),
    INDEX idx_operation (operation),
    INDEX idx_event_timestamp (event_timestamp)
);