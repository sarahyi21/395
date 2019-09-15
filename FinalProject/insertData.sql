
/*
SELECT * FROM Rooms;*/
use HospiceProject;
/*select * from Facilities;*/





INSERT INTO Facilities (facility_id, facility_name, location, phone_number, rooms_available) VALUES (1, 'Twin Palms Care Home', '3000 Cazador St, Los Angeles, CA 90065', '3232214321', 7);
INSERT INTO Facilities (facility_id, facility_name, location, phone_number, rooms_available) VALUES (2, 'Solheim Lutheran Home', '2236 Merton Ave, Los Angeles, CA 90041', '3232577518', 5);
INSERT INTO Facilities (facility_id, facility_name, location, phone_number, rooms_available) VALUES (3, 'El Molino Rose Villa', '1144 N El Molino Ave, Pasadena, CA 91104', '6266605750', 7);
INSERT INTO Facilities (facility_id, facility_name, location, phone_number, rooms_available) VALUES (4, 'The Bungalow Senior Care', '1314 E Woodbury Rd, Pasadena, CA 91104', '8183885543', 7);
INSERT INTO Facilities (facility_id, facility_name, location, phone_number, rooms_available) VALUES (5, 'Better Living & Care Home', '734 N La Jolla Ave, Los Angeles, CA 90046', '3236512733', 7);
 

  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (101, 1, 'Bedroom, bathroom, kitchen', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (102, 1, 'Bedroom, bathroom, kitchen', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (103, 1, 'Bedroom, bathroom, kitchen', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (104, 1, 'Bedroom, bathroom', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (105, 1, 'Bedroom, bathroom', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (106, 1, 'Bedroom, bathroom, kitchen', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (107, 1, 'Bedroom, bathroom', 'empty');
  
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (101, 2, 'Bedroom, bathroom, kitchen', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (102, 2, 'Bedroom, bathroom, kitchen', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (103, 2, 'Bedroom, bathroom, kitchen', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (104, 2, 'Bedroom, bathroom', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (105, 2, 'Bedroom, bathroom', 'occupied');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (106, 2, 'Bedroom, bathroom, kitchen', 'occupied');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (107, 2, 'Bedroom, bathroom', 'empty');
  
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (201, 3, 'Bedroom, bathroom, kitchen', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (202, 3, 'Bedroom, bathroom, kitchen', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (203, 3, 'Bedroom, bathroom, kitchen', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (204, 3, 'Bedroom, bathroom', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (205, 3, 'Bedroom, bathroom', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (206, 3, 'Bedroom, bathroom, kitchen', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (207, 3, 'Bedroom, bathroom', 'empty');
  
  
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (201, 4, 'Bedroom, bathroom, kitchen', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (202, 4, 'Bedroom, bathroom, kitchen', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (203, 4, 'Bedroom, bathroom, kitchen', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (204, 4, 'Bedroom, bathroom', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (205, 4, 'Bedroom, bathroom', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (206, 4, 'Bedroom, bathroom, kitchen', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (207, 4, 'Bedroom, bathroom', 'empty');

  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (301, 5, 'Bedroom, bathroom, kitchen', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (302, 5, 'Bedroom, bathroom, kitchen', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (303, 5, 'Bedroom, bathroom, kitchen', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (304, 5, 'Bedroom, bathroom', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (305, 5, 'Bedroom, bathroom', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (306, 5, 'Bedroom, bathroom, kitchen', 'empty');
  INSERT INTO Rooms (room_no, facility_id, amenities, available) VALUES (307, 5, 'Bedroom, bathroom', 'empty');
  
  
INSERT INTO Patients (patient_id, patient_name, reserved_facility, reserved_room, illness, notes) VALUES (1, "Andy Chin", 2, 105, "lovesick", "Likes to be with Celia Chen");
INSERT INTO Patients (patient_id, patient_name, reserved_facility, reserved_room, illness, notes) VALUES (2, "Celia Chen", 2, 106, "stroke by CS229 students", "Likes to be with Andy Chin");

  





