INSERT INTO vendor_category_rules (vendor_pattern, match_type, priority, category_id)
VALUES
    ('dominos',    'CONTAINS', 1, (SELECT id FROM categories WHERE name = 'Food')),
    ('kfc',        'EXACT',    1, (SELECT id FROM categories WHERE name = 'Food')),
    ('blinkit',    'EXACT',    1, (SELECT id FROM categories WHERE name = 'Food')),
    ('zepto',      'EXACT',    1, (SELECT id FROM categories WHERE name = 'Food')),
    ('dunzo',      'EXACT',    1, (SELECT id FROM categories WHERE name = 'Food')),
    ('spotify',    'EXACT',    1, (SELECT id FROM categories WHERE name = 'Entertainment')),
    ('hotstar',    'CONTAINS', 1, (SELECT id FROM categories WHERE name = 'Entertainment')),
    ('myntra',     'EXACT',    1, (SELECT id FROM categories WHERE name = 'Shopping')),
    ('meesho',     'EXACT',    1, (SELECT id FROM categories WHERE name = 'Shopping')),
    ('nykaa',      'EXACT',    1, (SELECT id FROM categories WHERE name = 'Shopping')),
    ('irctc',      'EXACT',    1, (SELECT id FROM categories WHERE name = 'Travel')),
    ('rapido',     'EXACT',    1, (SELECT id FROM categories WHERE name = 'Travel')),
    ('redbus',     'EXACT',    1, (SELECT id FROM categories WHERE name = 'Travel')),
    ('medplus',    'EXACT',    1, (SELECT id FROM categories WHERE name = 'Healthcare')),
    ('1mg',        'EXACT',    1, (SELECT id FROM categories WHERE name = 'Healthcare')),
    ('pharmeasy',  'EXACT',    1, (SELECT id FROM categories WHERE name = 'Healthcare'));