create table smsDeliveryReport(
	id bigint primary key auto_increment,
	messageId varchar(100) not null comment "Unique identifier of the message in the sms gateway system.",
	submittedOnDate datetime not null comment "Submission date and time.",
	doneOnDate datetime not null comment "Date and time the status has changed, or message delivery time when stat is set to 'DELIVRD'.",
	statusId int not null comment "Id of the SmsDeliveryReportStatus enum.",
	errorCode varchar(50) null comment "Additional error code in case of failure, provider specific."
);