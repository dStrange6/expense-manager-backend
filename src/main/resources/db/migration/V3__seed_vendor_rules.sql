WITH cats AS (SELECT id, name FROM categories)
INSERT INTO vendor_category_rules (vendor_pattern, match_type, priority, category_id) VALUES
    ('swiggy', 'EXACT', 1, (SELECT id FROM cats WHERE name = 'Food')),
    ('swiggy', 'CONTAINS', 2, (SELECT id FROM cats WHERE name = 'Food')),
    ('zomato', 'CONTAINS', 2, (SELECT id FROM cats WHERE name = 'Food')),
    ('amazon prime', 'EXACT', 1, (SELECT id FROM cats WHERE name = 'Entertainment')),
    ('netflix', 'EXACT', 1, (SELECT id FROM cats WHERE name = 'Entertainment')),
    ('amazon', 'CONTAINS', 2, (SELECT id FROM cats WHERE name = 'Shopping')),
    ('flipkart', 'CONTAINS', 1, (SELECT id FROM cats WHERE name = 'Shopping')),
    ('indigo', 'CONTAINS', 1, (SELECT id FROM cats WHERE name = 'Travel')),
    ('uber', 'EXACT', 1, (SELECT id FROM cats WHERE name = 'Travel')),
    ('uber eats', 'EXACT', 1, (SELECT id FROM cats WHERE name = 'Food')),
    ('apollo', 'CONTAINS', 1, (SELECT id FROM cats WHERE name = 'Healthcare')),
    ('indian oil', 'CONTAINS', 1, (SELECT id FROM cats WHERE name = 'Fuel')),
    ('jio', 'CONTAINS', 1, (SELECT id FROM cats WHERE name = 'Utilities')),
    ('airtel', 'CONTAINS', 1, (SELECT id FROM cats WHERE name = 'Utilities'));