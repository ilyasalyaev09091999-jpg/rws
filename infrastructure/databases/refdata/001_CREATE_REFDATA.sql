DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'refdata') THEN
        CREATE DATABASE refdata;
END IF;
END $$;