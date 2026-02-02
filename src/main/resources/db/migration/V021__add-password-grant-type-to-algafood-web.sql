UPDATE oauth2_registered_client
SET authorization_grant_types = 'refresh_token,authorization_code,password'
WHERE client_id = 'algafood-web';
