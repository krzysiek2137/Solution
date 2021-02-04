INSERT INTO PERMISSION (ID, PERMISSION_NAME) VALUES
  (1, 'Create user'),
  (2, 'Update user'),
  (3, 'Delete user'),
  (4, 'List users');

INSERT INTO ROLE (ID, ROLE_NAME) VALUES
  (-1, 'ADMIN');

INSERT INTO USER (USER_NAME, PASSWORD, ROLE_ID) VALUES
  ('ADMIN', '$2a$10$RTfx0QfVrKmmg7IEFy2YL.IgKJE.B.pd1QREFCFYu3RrMVV.behl.', -1);