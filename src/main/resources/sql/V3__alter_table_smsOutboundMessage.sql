ALTER TABLE `smsOutboundMessage` ADD `numberOfSegments` INT NULL;
ALTER TABLE `smsOutboundMessage` ADD `smsErrorCodeId` INT NULL;
ALTER TABLE `smsOutboundMessage` DROP `deliveryErrorMessage`;