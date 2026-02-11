UPDATE oauth2_registered_client
SET token_settings = REPLACE(token_settings, '"value":"self-contained"', '"value":"reference"')
WHERE client_id = 'algafood-web';
