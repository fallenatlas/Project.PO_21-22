package ggc.app.lookups;

/**
 * Menu entries.
 */
interface Label {

  /** Menu title. */
  String TITLE = "Consultas";

  /** List trips from location. */
  String PRODUCTS_BY_PARTNER = "Produtos comprados por parceiro";

  /** List trips to location. */
  String PRODUCTS_UNDER_PRICE = "Produtos com Preço Abaixo de Limite";

  /** List of available trips from location. */
  String PAID_BY_PARTNER = "Facturas pagas por parceiro";

  /** List of available trips to location. */
  String PAID_LATE = "Facturas Pagas com Atraso";

  /** List trips from location. */
  String PARTNERS_BY_PRODUCT = "Parceiros que Compram um Produto";

  String PRODUCTS_WITH_LEAST_AMOUNT_OF_STOCK = "Produtos com menor stock";

  String PRODUCTS_WITH_LEAST_ACQUISITIONS_VALUE = "Parceiro com menor valor de aquisicoes";

  String PRODUCTS_WITH_LEAST_TOTAL_PRICE = "Produto com menor valor total";

  String PARTNER_WITH_MOST_SALES_VALUE = "Parceiro com maior valor de vendas";

}