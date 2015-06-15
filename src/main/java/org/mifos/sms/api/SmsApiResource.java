package org.mifos.sms.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mifos.sms.data.DeliveryReportRequestData;
import org.mifos.sms.domain.SmsOutboundMessage;
import org.mifos.sms.helper.HelperClass.ApiResponse;
import org.mifos.sms.helper.HelperClass.JsonConverter;
import org.mifos.sms.helper.HttpResponseStatusCode;
import org.mifos.sms.service.ReadSmsOutboundMessageService;
import org.mifos.sms.service.WriteSmsOutboundMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.gson.reflect.TypeToken;

@Path("/sms")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@Component
@Scope("singleton")
public class SmsApiResource {
	private final WriteSmsOutboundMessageService writeSmsOutboundMessageService;
	private final ReadSmsOutboundMessageService readSmsOutboundMessageService;
	private static final Logger logger = LoggerFactory.getLogger(SmsApiResource.class);
	
	@Autowired
	public SmsApiResource(final WriteSmsOutboundMessageService writeSmsOutboundMessageService, 
			final ReadSmsOutboundMessageService readSmsOutboundMessageService) {
		this.writeSmsOutboundMessageService = writeSmsOutboundMessageService;
		this.readSmsOutboundMessageService = readSmsOutboundMessageService;
	}
	
	@POST
	@Path("/report")
	public Response getDeliveryReport(final String apiRequestJsonString) {
		Response response;
		
		// convert API request JSON string to object of class DeliveryReportRequestData
		Map<String, Object> deliveryReportRequestData = JsonConverter.fromJson(apiRequestJsonString, DeliveryReportRequestData.class);
		
		// if the GSON class failed in the conversion, the value of the "hasError" key is set to true
		if(!(Boolean)deliveryReportRequestData.get("hasError")) {
			DeliveryReportRequestData deliveryReportRequest = (DeliveryReportRequestData)deliveryReportRequestData.get("object");
			List<Long> externalIds = deliveryReportRequest.getExternalIds();
			String mifosTenantIdentifier = deliveryReportRequest.getMifosTenantIdentifier();
			
			// make sure a mifos tenant identifier and list of external ids are provided
			if(StringUtils.isEmpty(mifosTenantIdentifier) || (externalIds.size() < 1)) {
				response = ApiResponse.error(HttpResponseStatusCode.BAD_REQUEST, "Validation errors exist");
			}
			
			else {
				try {
					// attempt to fetch the SMS messages
					response = ApiResponse.success(readSmsOutboundMessageService.findAll(externalIds, mifosTenantIdentifier));
				}
				
				catch(Exception e) {
					// catch any exception and return as an internal server error
					response = ApiResponse.error(HttpResponseStatusCode.INTERNAL_SERVER_ERROR, e.getMessage());
					
					// log error message
					logger.error(e.getMessage());
				}
			}
		}
		
		else {
			response = ApiResponse.error(HttpResponseStatusCode.BAD_REQUEST, (String)deliveryReportRequestData.get("errorMessage"));
		}
		
		return response;
	}
	
	@SuppressWarnings("unchecked")
	@POST
	@Path("/queue")
	public Response addToQueue(final String apiRequestJsonString) {
		Response response;
		
		// convert the API request JSON string to list of SmsOutboundMessage objects
		Map<String, Object> smsOutboundMessages = JsonConverter.fromJson(apiRequestJsonString, 
				new TypeToken<List<SmsOutboundMessage>>(){}.getType());
		List<SmsOutboundMessage> smsOutboundMessageList = new ArrayList<>();
		
		// if the GSON class failed in the conversion, the value of the "hasError" key is set to true
		if(!(Boolean)smsOutboundMessages.get("hasError")) {
			// get the list of SmsOutboundMessage objects from the map
			smsOutboundMessageList = (List<SmsOutboundMessage>) smsOutboundMessages.get("object");
			
			try {
				// attempt to add the SMS message to the smsOuntboundMessage table
				response = ApiResponse.success(writeSmsOutboundMessageService.create(smsOutboundMessageList));
			}
			
			catch(Exception e) {
				// catch any exception and return as an internal server error
				response = ApiResponse.error(HttpResponseStatusCode.INTERNAL_SERVER_ERROR, e.getMessage());
				
				// log error message
				logger.error(e.getMessage());
			}
		}
		
		else {
			response = ApiResponse.error(HttpResponseStatusCode.BAD_REQUEST, (String)smsOutboundMessages.get("errorMessage"));
		}
		
		return response;
	}
}
