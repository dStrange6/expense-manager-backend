INSERT INTO categories (name) VALUES
    ('Food'),
    ('Travel'),
    ('Shopping'),
    ('Healthcare'),
    ('Entertainment'),
    ('Fuel'),
    ('Utilities'),
    ('Banking'),
    ('Uncategorised')
ON CONFLICT (name) DO NOTHING;