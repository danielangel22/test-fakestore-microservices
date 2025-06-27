-- Solo crea el usuario si no existe y otorga permisos
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_user WHERE usename = 'admin') THEN
        CREATE USER admin WITH PASSWORD 'admin123';
        ALTER USER admin WITH SUPERUSER;
    END IF;
END
$$;

GRANT ALL PRIVILEGES ON DATABASE shopping_cart TO admin;