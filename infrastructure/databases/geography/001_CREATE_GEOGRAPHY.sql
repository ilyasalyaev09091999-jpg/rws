DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'geography') THEN
        CREATE DATABASE geography;
END IF;
END $$;