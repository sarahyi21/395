/* drop database HospiceProject; */

create database HospiceProject;
use HospiceProject;




create table Users(
username varchar(50),
hashedpw varchar(200),
primary key (username)
);

create table Facilities(
    facility_id int not null,
    facility_name varchar(60) not null,
	location varchar(60),
    phone_number varchar(15),
	rooms_available int not null,
    primary key (facility_id)  
);
create table Rooms(
	room_no int not null,
    facility_id int not null,
    amenities varchar(150),
    available varchar(20) not null,
    primary key(room_no, facility_id),
    foreign key (facility_id) references Facilities(facility_id)
);
create table Patients(
	patient_id int not null,
    patient_name varchar(60) not null,
    reserved_facility int,
    reserved_room int,
    illness varchar(60) not null,
    notes varchar(60) not null,
    primary key (patient_id),
    foreign key (reserved_facility) references Facilities(facility_id),
    foreign key (reserved_room) references Rooms(room_no)
);


create table Bookings(
	booking_no int not null,
    patient_id int not null,
    booking_time datetime not null,
    primary key (booking_no),
    foreign key (patient_id) references Patients(patient_id)
);

