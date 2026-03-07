DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'archive') THEN
        CREATE DATABASE archive;
    END IF;
END $$;
