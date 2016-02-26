package com.viewbill.bo;

public class Dto {
	public static class TMP {
		public java.math.BigDecimal n;
		public String s;
		public java.sql.Timestamp t;
		public TMP(java.math.BigDecimal n, String s, java.sql.Timestamp t) {
			this.n = n;
			this.s = s;
			this.t = t;
		}
	}

	public static class WFA_CFDITT {
		public String cpt_numcptfac;
		public String cfd_codaplemi;
		public String cfd_idtope;
		public String cus_numcus;
		public String cfd_libgrp;
		public String cfd_codac;
		public String cat_idtcat;
		public WFA_CFDITT(String cpt_numcptfac, String cfd_codaplemi, String cfd_idtope, String cus_numcus, String cfd_libgrp, String cfd_codac, String cat_idtcat) {
			this.cpt_numcptfac = cpt_numcptfac;
			this.cfd_codaplemi = cfd_codaplemi;
			this.cfd_idtope = cfd_idtope;
			this.cus_numcus = cus_numcus;
			this.cfd_libgrp = cfd_libgrp;
			this.cfd_codac = cfd_codac;
			this.cat_idtcat = cat_idtcat;
		}
	}

	public static class WFA_CONFIG {
		public String property_name;
		public String property_value;
		public WFA_CONFIG(String property_name, String property_value) {
			this.property_name = property_name;
			this.property_value = property_value;
		}
	}

	public static class WFA_CTC {
		public java.math.BigDecimal ctc_id;
		public String ctc_ext_id;
		public String ctc_first_name;
		public String ctc_last_name;
		public String ctc_email;
		public WFA_CTC(java.math.BigDecimal ctc_id, String ctc_ext_id, String ctc_first_name, String ctc_last_name, String ctc_email) {
			this.ctc_id = ctc_id;
			this.ctc_ext_id = ctc_ext_id;
			this.ctc_first_name = ctc_first_name;
			this.ctc_last_name = ctc_last_name;
			this.ctc_email = ctc_email;
		}
	}

	public static class WFA_CUSFIL {
		public java.math.BigDecimal file_oid;
		public String file_name;
		public java.math.BigDecimal file_size;
		public String description;
		public Object creation_date;
		public java.sql.Timestamp expiry_date;
		public WFA_CUSFIL(java.math.BigDecimal file_oid, String file_name, java.math.BigDecimal file_size, String description, Object creation_date, java.sql.Timestamp expiry_date) {
			this.file_oid = file_oid;
			this.file_name = file_name;
			this.file_size = file_size;
			this.description = description;
			this.creation_date = creation_date;
			this.expiry_date = expiry_date;
		}
	}

	public static class WFA_EI_BAN_ACTION_TRACKING {
		public String cpt_numcptfac;
		public String email;
		public String last_action;
		public java.sql.Timestamp action_date;
		public String utl_codutl;
		public WFA_EI_BAN_ACTION_TRACKING(String cpt_numcptfac, String email, String last_action, java.sql.Timestamp action_date, String utl_codutl) {
			this.cpt_numcptfac = cpt_numcptfac;
			this.email = email;
			this.last_action = last_action;
			this.action_date = action_date;
			this.utl_codutl = utl_codutl;
		}
	}

	public static class WFA_EI_BAN_CORE {
		public String cpt_numcptfac;
		public String email;
		public java.sql.Timestamp modification_date;
		public String validation_token;
		public String core_notified;
		public WFA_EI_BAN_CORE(String cpt_numcptfac, String email, java.sql.Timestamp modification_date, String validation_token, String core_notified) {
			this.cpt_numcptfac = cpt_numcptfac;
			this.email = email;
			this.modification_date = modification_date;
			this.validation_token = validation_token;
			this.core_notified = core_notified;
		}
	}

	public static class WFA_EI_BAN_VIEWBILL {
		public java.math.BigDecimal subscription_id;
		public String cpt_numcptfac;
		public WFA_EI_BAN_VIEWBILL(java.math.BigDecimal subscription_id, String cpt_numcptfac) {
			this.subscription_id = subscription_id;
			this.cpt_numcptfac = cpt_numcptfac;
		}
	}

	public static class WFA_EI_FEATURE {
		public String email;
		public String format;
		public String language;
		public String zip_attachments;
		public java.math.BigDecimal max_size_in_bytes;
		public WFA_EI_FEATURE(String email, String format, String language, String zip_attachments, java.math.BigDecimal max_size_in_bytes) {
			this.email = email;
			this.format = format;
			this.language = language;
			this.zip_attachments = zip_attachments;
			this.max_size_in_bytes = max_size_in_bytes;
		}
	}

	public static class WFA_EI_MONITORING {
		public java.math.BigDecimal monitoring_id;
		public String period;
		public String cus_numcus;
		public String token;
		public WFA_EI_MONITORING(java.math.BigDecimal monitoring_id, String period, String cus_numcus, String token) {
			this.monitoring_id = monitoring_id;
			this.period = period;
			this.cus_numcus = cus_numcus;
			this.token = token;
		}
	}

	public static class WFA_EI_MONITORING_FILE {
		public java.math.BigDecimal monitoring_id;
		public String folder_path;
		public String file_name;
		public java.math.BigDecimal file_size_in_bytes;
		public WFA_EI_MONITORING_FILE(java.math.BigDecimal monitoring_id, String folder_path, String file_name, java.math.BigDecimal file_size_in_bytes) {
			this.monitoring_id = monitoring_id;
			this.folder_path = folder_path;
			this.file_name = file_name;
			this.file_size_in_bytes = file_size_in_bytes;
		}
	}

	public static class WFA_EI_MONITORING_HISTORY {
		public java.math.BigDecimal monitoring_id;
		public java.sql.Timestamp action_date;
		public String email_address;
		public String email_status;
		public java.math.BigDecimal is_core_address;
		public String author;
		public String user_comment;
		public WFA_EI_MONITORING_HISTORY(java.math.BigDecimal monitoring_id, java.sql.Timestamp action_date, String email_address, String email_status, java.math.BigDecimal is_core_address, String author, String user_comment) {
			this.monitoring_id = monitoring_id;
			this.action_date = action_date;
			this.email_address = email_address;
			this.email_status = email_status;
			this.is_core_address = is_core_address;
			this.author = author;
			this.user_comment = user_comment;
		}
	}

	public static class WFA_EI_REJ_INVOICE {
		public java.math.BigDecimal monitoring_id;
		public java.sql.Timestamp insert_date;
		public WFA_EI_REJ_INVOICE(java.math.BigDecimal monitoring_id, java.sql.Timestamp insert_date) {
			this.monitoring_id = monitoring_id;
			this.insert_date = insert_date;
		}
	}

	public static class WFA_EI_REJ_VALIDATION {
		public String token;
		public java.sql.Timestamp insert_date;
		public WFA_EI_REJ_VALIDATION(String token, java.sql.Timestamp insert_date) {
			this.token = token;
			this.insert_date = insert_date;
		}
	}

	public static class WFA_EI_SUBSCRIPTION_VIEWBILL {
		public java.math.BigDecimal subscription_id;
		public String email;
		public java.math.BigDecimal activation;
		public WFA_EI_SUBSCRIPTION_VIEWBILL(java.math.BigDecimal subscription_id, String email, java.math.BigDecimal activation) {
			this.subscription_id = subscription_id;
			this.email = email;
			this.activation = activation;
		}
	}

	public static class WFA_EI_VALIDATION_EMAIL {
		public String token;
		public String email;
		public String status;
		public java.sql.Timestamp update_status_date;
		public WFA_EI_VALIDATION_EMAIL(String token, String email, String status, java.sql.Timestamp update_status_date) {
			this.token = token;
			this.email = email;
			this.status = status;
			this.update_status_date = update_status_date;
		}
	}

	public static class WFA_FACPDF {
		public String fac_numfacree;
		public java.math.BigDecimal fac_numfac;
		public String cpt_numcptfac;
		public String fac_nomdom;
		public java.math.BigDecimal fac_mnttotttc;
		public String pdf_nomper;
		public String pdf_nomrep;
		public String pdf_nomfic;
		public String pdf_pfxtab;
		public String pdf_nomrepcsv;
		public String pdf_nomficcsv;
		public String pdf_nomrepxml;
		public String pdf_nomficxml;
		public String pdf_nomrepf94;
		public String pdf_nomficf94;
		public String lbf_codfac;
		public java.sql.Timestamp fac_datfac;
		public String fam_codfamfac;
		public String lbf_codree;
		public String cus_numcus;
		public java.math.BigDecimal env_id;
		public String fac_devfac;
		public java.math.BigDecimal fac_mnttotht;
		public java.math.BigDecimal is_available;
		public java.math.BigDecimal is_out_of_scope;
		public java.sql.Timestamp fac_period_from;
		public java.sql.Timestamp fac_period_to;
		public String notif;
		public WFA_FACPDF(String fac_numfacree, java.math.BigDecimal fac_numfac, String cpt_numcptfac, String fac_nomdom, java.math.BigDecimal fac_mnttotttc, String pdf_nomper, String pdf_nomrep, String pdf_nomfic, String pdf_pfxtab, String pdf_nomrepcsv, String pdf_nomficcsv, String pdf_nomrepxml, String pdf_nomficxml, String pdf_nomrepf94, String pdf_nomficf94, String lbf_codfac, java.sql.Timestamp fac_datfac, String fam_codfamfac, String lbf_codree, String cus_numcus, java.math.BigDecimal env_id, String fac_devfac, java.math.BigDecimal fac_mnttotht, java.math.BigDecimal is_available, java.math.BigDecimal is_out_of_scope, java.sql.Timestamp fac_period_from, java.sql.Timestamp fac_period_to, String notif) {
			this.fac_numfacree = fac_numfacree;
			this.fac_numfac = fac_numfac;
			this.cpt_numcptfac = cpt_numcptfac;
			this.fac_nomdom = fac_nomdom;
			this.fac_mnttotttc = fac_mnttotttc;
			this.pdf_nomper = pdf_nomper;
			this.pdf_nomrep = pdf_nomrep;
			this.pdf_nomfic = pdf_nomfic;
			this.pdf_pfxtab = pdf_pfxtab;
			this.pdf_nomrepcsv = pdf_nomrepcsv;
			this.pdf_nomficcsv = pdf_nomficcsv;
			this.pdf_nomrepxml = pdf_nomrepxml;
			this.pdf_nomficxml = pdf_nomficxml;
			this.pdf_nomrepf94 = pdf_nomrepf94;
			this.pdf_nomficf94 = pdf_nomficf94;
			this.lbf_codfac = lbf_codfac;
			this.fac_datfac = fac_datfac;
			this.fam_codfamfac = fam_codfamfac;
			this.lbf_codree = lbf_codree;
			this.cus_numcus = cus_numcus;
			this.env_id = env_id;
			this.fac_devfac = fac_devfac;
			this.fac_mnttotht = fac_mnttotht;
			this.is_available = is_available;
			this.is_out_of_scope = is_out_of_scope;
			this.fac_period_from = fac_period_from;
			this.fac_period_to = fac_period_to;
			this.notif = notif;
		}
	}

	public static class WFA_FILAXS {
		public java.math.BigDecimal file_oid;
		public String adee_type;
		public String adee_id;
		public WFA_FILAXS(java.math.BigDecimal file_oid, String adee_type, String adee_id) {
			this.file_oid = file_oid;
			this.adee_type = adee_type;
			this.adee_id = adee_id;
		}
	}

	public static class WFA_FILTERING {
		public String utl_codutl;
		public String item;
		public String value;
		public WFA_FILTERING(String utl_codutl, String item, String value) {
			this.utl_codutl = utl_codutl;
			this.item = item;
			this.value = value;
		}
	}

	public static class WFA_INVANA {
		public String invoice_number;
		public String service_description;
		public String charge_type;
		public String charge_description;
		public java.math.BigDecimal charge_amount;
		public java.math.BigDecimal tax_amount;
		public String invoicing_currency;
		public String site;
		public WFA_INVANA(String invoice_number, String service_description, String charge_type, String charge_description, java.math.BigDecimal charge_amount, java.math.BigDecimal tax_amount, String invoicing_currency, String site) {
			this.invoice_number = invoice_number;
			this.service_description = service_description;
			this.charge_type = charge_type;
			this.charge_description = charge_description;
			this.charge_amount = charge_amount;
			this.tax_amount = tax_amount;
			this.invoicing_currency = invoicing_currency;
			this.site = site;
		}
	}

	public static class WFA_NOTIF {
		public java.math.BigDecimal notif_id;
		public String utl_codutl;
		public String notif_type;
		public String notif_email;
		public java.math.BigDecimal notif_activation;
		public String format;
		public String language;
		public WFA_NOTIF(java.math.BigDecimal notif_id, String utl_codutl, String notif_type, String notif_email, java.math.BigDecimal notif_activation, String format, String language) {
			this.notif_id = notif_id;
			this.utl_codutl = utl_codutl;
			this.notif_type = notif_type;
			this.notif_email = notif_email;
			this.notif_activation = notif_activation;
			this.format = format;
			this.language = language;
		}
	}

	public static class WFA_NOTIF_EXCLUDED_BAN {
		public java.math.BigDecimal notif_id;
		public String cpt_numcptfac;
		public WFA_NOTIF_EXCLUDED_BAN(java.math.BigDecimal notif_id, String cpt_numcptfac) {
			this.notif_id = notif_id;
			this.cpt_numcptfac = cpt_numcptfac;
		}
	}

	public static class WFA_REPCSV {
		public String cpt_numcptfac;
		public String csv_nomper;
		public String csv_format;
		public String csv_nomrep;
		public String csv_nomfic;
		public java.math.BigDecimal env_id;
		public java.math.BigDecimal is_available;
		public java.math.BigDecimal is_out_of_scope;
		public String fac_numfacree;
		public WFA_REPCSV(String cpt_numcptfac, String csv_nomper, String csv_format, String csv_nomrep, String csv_nomfic, java.math.BigDecimal env_id, java.math.BigDecimal is_available, java.math.BigDecimal is_out_of_scope, String fac_numfacree) {
			this.cpt_numcptfac = cpt_numcptfac;
			this.csv_nomper = csv_nomper;
			this.csv_format = csv_format;
			this.csv_nomrep = csv_nomrep;
			this.csv_nomfic = csv_nomfic;
			this.env_id = env_id;
			this.is_available = is_available;
			this.is_out_of_scope = is_out_of_scope;
			this.fac_numfacree = fac_numfacree;
		}
	}

	public static class WFA_RSC {
		public String rsc_code;
		public String rsc_country;
		public String rsc_name;
		public java.math.BigDecimal local_printing;
		public String country_name;
		public WFA_RSC(String rsc_code, String rsc_country, String rsc_name, java.math.BigDecimal local_printing, String country_name) {
			this.rsc_code = rsc_code;
			this.rsc_country = rsc_country;
			this.rsc_name = rsc_name;
			this.local_printing = local_printing;
			this.country_name = country_name;
		}
	}

	public static class WFA_TRACES {
		public String utl_codutl;
		public java.sql.Timestamp trc_datfinses;
		public java.math.BigDecimal trc_nbredt;
		public String trc_logged_as_user;
		public String trc_module_code;
		public String trc_action_code;
		public String trc_action_sub_code;
		public String trc_object_class;
		public String trc_object_id;
		public WFA_TRACES(String utl_codutl, java.sql.Timestamp trc_datfinses, java.math.BigDecimal trc_nbredt, String trc_logged_as_user, String trc_module_code, String trc_action_code, String trc_action_sub_code, String trc_object_class, String trc_object_id) {
			this.utl_codutl = utl_codutl;
			this.trc_datfinses = trc_datfinses;
			this.trc_nbredt = trc_nbredt;
			this.trc_logged_as_user = trc_logged_as_user;
			this.trc_module_code = trc_module_code;
			this.trc_action_code = trc_action_code;
			this.trc_action_sub_code = trc_action_sub_code;
			this.trc_object_class = trc_object_class;
			this.trc_object_id = trc_object_id;
		}
	}

	public static class WFA_UTL {
		public String utl_codutl;
		public String utl_isadm;
		public java.math.BigDecimal utl_ctc_id;
		public String utl_siu_id;
		public WFA_UTL(String utl_codutl, String utl_isadm, java.math.BigDecimal utl_ctc_id, String utl_siu_id) {
			this.utl_codutl = utl_codutl;
			this.utl_isadm = utl_isadm;
			this.utl_ctc_id = utl_ctc_id;
			this.utl_siu_id = utl_siu_id;
		}
	}

	public static class WFA_UTLCPT {
		public String utl_codutl;
		public String cpt_numcptfac;
		public WFA_UTLCPT(String utl_codutl, String cpt_numcptfac) {
			this.utl_codutl = utl_codutl;
			this.cpt_numcptfac = cpt_numcptfac;
		}
	}

	public static class WFA_UTLCUS {
		public String utl_codutl;
		public String cus_numcus;
		public WFA_UTLCUS(String utl_codutl, String cus_numcus) {
			this.utl_codutl = utl_codutl;
			this.cus_numcus = cus_numcus;
		}
	}

	public static class WFA_VIEW_CFDITT {
		public String utl_codutl;
		public String cus_numcus;
		public String cpt_numcptfac;
		public String link_utl_cus;
		public String cfd_codaplemi;
		public String cfd_idtope;
		public String cfd_libgrp;
		public String cfd_codac;
		public String cat_idtcat;
		public WFA_VIEW_CFDITT(String utl_codutl, String cus_numcus, String cpt_numcptfac, String link_utl_cus, String cfd_codaplemi, String cfd_idtope, String cfd_libgrp, String cfd_codac, String cat_idtcat) {
			this.utl_codutl = utl_codutl;
			this.cus_numcus = cus_numcus;
			this.cpt_numcptfac = cpt_numcptfac;
			this.link_utl_cus = link_utl_cus;
			this.cfd_codaplemi = cfd_codaplemi;
			this.cfd_idtope = cfd_idtope;
			this.cfd_libgrp = cfd_libgrp;
			this.cfd_codac = cfd_codac;
			this.cat_idtcat = cat_idtcat;
		}
	}

	public static class WFA_VIEW_EI_ADDRESS_STATUS {
		public String email;
		public String validation_status;
		public WFA_VIEW_EI_ADDRESS_STATUS(String email, String validation_status) {
			this.email = email;
			this.validation_status = validation_status;
		}
	}

	public static class WFA_VIEW_EI_BAN {
		public String cpt_numcptfac;
		public String email;
		public String origin;
		public WFA_VIEW_EI_BAN(String cpt_numcptfac, String email, String origin) {
			this.cpt_numcptfac = cpt_numcptfac;
			this.email = email;
			this.origin = origin;
		}
	}

	public static class WFA_VIEW_EI_MONITORING {
		public java.math.BigDecimal monitoring_id;
		public String period;
		public String cus_numcus;
		public String token;
		public String email_address;
		public String email_status;
		public java.sql.Timestamp action_date;
		public String author;
		public String user_comment;
		public java.sql.Timestamp sending_date;
		public WFA_VIEW_EI_MONITORING(java.math.BigDecimal monitoring_id, String period, String cus_numcus, String token, String email_address, String email_status, java.sql.Timestamp action_date, String author, String user_comment, java.sql.Timestamp sending_date) {
			this.monitoring_id = monitoring_id;
			this.period = period;
			this.cus_numcus = cus_numcus;
			this.token = token;
			this.email_address = email_address;
			this.email_status = email_status;
			this.action_date = action_date;
			this.author = author;
			this.user_comment = user_comment;
			this.sending_date = sending_date;
		}
	}

	public static class WFA_VIEW_LOGIN_BAN {
		public String login;
		public String firstname;
		public String lastname;
		public String email;
		public String ban;
		public WFA_VIEW_LOGIN_BAN(String login, String firstname, String lastname, String email, String ban) {
			this.login = login;
			this.firstname = firstname;
			this.lastname = lastname;
			this.email = email;
			this.ban = ban;
		}
	}

	public static class WFA_VIEW_LOGIN_CUSCODE {
		public String login;
		public String firstname;
		public String lastname;
		public String email;
		public String cuscode;
		public WFA_VIEW_LOGIN_CUSCODE(String login, String firstname, String lastname, String email, String cuscode) {
			this.login = login;
			this.firstname = firstname;
			this.lastname = lastname;
			this.email = email;
			this.cuscode = cuscode;
		}
	}

	public static class WFA_VIEW_LOGIN_CUSCODE_BAN {
		public String login;
		public String firstname;
		public String lastname;
		public String email;
		public String cuscode;
		public String ban;
		public WFA_VIEW_LOGIN_CUSCODE_BAN(String login, String firstname, String lastname, String email, String cuscode, String ban) {
			this.login = login;
			this.firstname = firstname;
			this.lastname = lastname;
			this.email = email;
			this.cuscode = cuscode;
			this.ban = ban;
		}
	}

}

