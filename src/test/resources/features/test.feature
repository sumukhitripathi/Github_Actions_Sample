Feature: Google homepage screenshot upload

  Scenario: Launch Google, capture screenshot and upload to S3
    When navigate to google page
    Then screenshot of page is captured
    Then screenshot uploaded to S3 bucket