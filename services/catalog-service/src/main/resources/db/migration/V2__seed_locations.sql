INSERT INTO locations (code, type, name, country, city, lat, lon, searchable_text) VALUES
('HEL','airport','Helsinki Airport','Finland','Helsinki',60.3172,24.9633,lower(concat_ws(' ','HEL','Helsinki Airport','Helsinki','Finland'))),
('BCN','airport','Barcelona El Prat Airport','Spain','Barcelona',41.2974,2.0833,lower(concat_ws(' ','BCN','Barcelona El Prat Airport','Barcelona','Spain'))),
('LHR','airport','London Heathrow Airport','United Kingdom','London',51.4700,-0.4543,lower(concat_ws(' ','LHR','London Heathrow Airport','London','United Kingdom'))),
('CDG','airport','Paris Charles de Gaulle Airport','France','Paris',49.0097,2.5479,lower(concat_ws(' ','CDG','Paris Charles de Gaulle Airport','Paris','France'))),
('FRA','airport','Frankfurt Airport','Germany','Frankfurt',50.0379,8.5622,lower(concat_ws(' ','FRA','Frankfurt Airport','Frankfurt','Germany'))),
('AMS','airport','Amsterdam Airport Schiphol','Netherlands','Amsterdam',52.3105,4.7683,lower(concat_ws(' ','AMS','Amsterdam Airport Schiphol','Amsterdam','Netherlands'))),
('MAD','airport','Adolfo Suárez Madrid–Barajas Airport','Spain','Madrid',40.4983,-3.5676,lower(concat_ws(' ','MAD','Adolfo Suárez Madrid–Barajas Airport','Madrid','Spain'))),
('FCO','airport','Rome Fiumicino Airport','Italy','Rome',41.8003,12.2389,lower(concat_ws(' ','FCO','Rome Fiumicino Airport','Rome','Italy'))),
('ARN','airport','Stockholm Arlanda Airport','Sweden','Stockholm',59.6519,17.9186,lower(concat_ws(' ','ARN','Stockholm Arlanda Airport','Stockholm','Sweden'))),
('OSL','airport','Oslo Airport','Norway','Oslo',60.1939,11.1004,lower(concat_ws(' ','OSL','Oslo Airport','Oslo','Norway'))),
('CPH','airport','Copenhagen Airport','Denmark','Copenhagen',55.6181,12.6561,lower(concat_ws(' ','CPH','Copenhagen Airport','Copenhagen','Denmark'))),
('JFK','airport','John F. Kennedy International Airport','USA','New York',40.6413,-73.7781,lower(concat_ws(' ','JFK','John F. Kennedy International Airport','New York','USA'))),
('LON','city','London','United Kingdom','London',51.5072,-0.1276,lower(concat_ws(' ','LON','London','London','United Kingdom'))),
('PAR','city','Paris','France','Paris',48.8566,2.3522,lower(concat_ws(' ','PAR','Paris','Paris','France'))),
('NYC','city','New York','USA','New York',40.7128,-74.0060,lower(concat_ws(' ','NYC','New York','New York','USA')))
ON CONFLICT (code) DO NOTHING;
