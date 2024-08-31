CREATE
DATABASE venus;

CREATE TABLE links
(
    id           BIGINT PRIMARY KEY COMMENT 'Unique identifier for each short link',
    code         VARCHAR(8) UNIQUE   NOT NULL UNIQUE COMMENT 'Unique code for the short link, e.g., abc123',
    original_url varchar(500) UNIQUE NOT NULL COMMENT 'The original URL',
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Timestamp when the short link was created',
    redirect     INT(1) DEFAULT 302 COMMENT 'redirect code: 301 or 302',
    expires_at   DATETIME COMMENT 'Expiration timestamp for the short link, if applicable',
    is_active    TINYINT(1) DEFAULT 1 COMMENT 'Indicates if the short link is currently active, 0 = inactive, 1 = active',
    INDEX        idx_original_url (original_url)
) ENGINE=InnoDB;

CREATE TABLE statistics
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Unique identifier for each click record',
    link_id    INT NOT NULL COMMENT 'Foreign key referencing the associated short link',
    clicked_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Timestamp when the click occurred',
    ip         VARCHAR(45) COMMENT 'IP address of the user who clicked the link',
    user_agent VARCHAR(500) COMMENT 'Browser information (User-Agent string) of the user who clicked the link',
    referer    VARCHAR(500) COMMENT 'The referring page URL from which the user clicked the short link'
) ENGINE=MyISAM;

CREATE TABLE geo
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Unique identifier for each geo location record',
    click_id  INT NOT NULL COMMENT 'Foreign key referencing the associated click record',
    country   VARCHAR(50) COMMENT 'Country derived from the IP address',
    city      VARCHAR(50) COMMENT 'City derived from the IP address',
    latitude  DECIMAL(10, 8) COMMENT 'Latitude for the location',
    longitude DECIMAL(11, 8) COMMENT 'Longitude for the location'
) ENGINE=MyISAM;
