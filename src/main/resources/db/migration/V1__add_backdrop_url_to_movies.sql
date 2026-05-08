-- Add backdrop_url column to movies table
-- This column stores the URL of the horizontal backdrop image used in the featured carousel
ALTER TABLE movies ADD COLUMN backdrop_url VARCHAR(2048);
