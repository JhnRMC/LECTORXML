package com.readerxml.util;

public enum Etiqueta {
    INVOICE("Invoice"),
    CREDITNOTE("CreditNote"),
    DEBITNOTE("DebitNote"),
    CBC_INVOICETYPECODE("cbc:InvoiceTypeCode"),
    CBC_ID("cbc:ID"),
    DOCUMENTCURRENCYCODE("cbc:DocumentCurrencyCode"),
    CBC_ISSUEDATE("cbc:IssueDate"),
    CAC_PARTYIDENTIFICATION("cac:PartyIdentification"),
    CAC_ACCOUNTINGSUPPLIERPARTY("cac:AccountingSupplierParty"),
    CBC_REGISTRATIONNAME("cbc:RegistrationName"),
    CAC_ACCOUNTINGCUSTOMERPARTY("cac:AccountingCustomerParty"),
    CAC_INVOICELINE("cac:InvoiceLine"),
    CAC_ITEM("cac:Item"),
    CAC_SELLERSITEMIDENTIFICATION("cac:SellersItemIdentification"),
    CBC_DESCRIPTION("cbc:Description"),
    CBC_INVOICEDQUANTITY("cbc:InvoicedQuantity"),
    CAC_PRICE("cac:Price"),
    CBC_PRICEAMOUNT("cbc:PriceAmount"),
    CBC_LINEEXTENSIONAMOUNT("cbc:LineExtensionAmount"),
    EXT_UBLEXTENSIONS("ext:UBLExtensions"),
    SAC_ADDITIONALMONETARYTOTAL("sac:AdditionalMonetaryTotal"),
    CBC_PAYABLEAMOUNT("cbc:PayableAmount"),
    CAC_TAXTOTAL("cac:TaxTotal"),
    CBC_TAXAMOUNT("cbc:TaxAmount"),
    CAC_LEGALMONETARYTOTAL("cac:LegalMonetaryTotal"),
    CBC_CUSTOMERASSIGNEDACCOUNTID("cbc:CustomerAssignedAccountID"),
    CBC_UBLVERSIONID("cbc:UBLVersionID"),
    CAC_CREDITNOTELINE("cac:CreditNoteLine"),
    CAC_CREDITEDQUANTITY("cbc:CreditedQuantity"),
    CAC_DEBITNOTELINE("cac:DebitNoteLine"),
    CAC_DEBITEDQUANTITY("cbc:DebitedQuantity");

    private String etiqueta;

    Etiqueta(String etiqueta){
        this.etiqueta = etiqueta;
    }

    public String obtenerEtiqueta() {
        return etiqueta;
    }
}
