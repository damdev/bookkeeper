package com.github.damdev.increase.bookkeeper.increasebookkeeper

import java.time.LocalDate

import com.github.damdev.increase.bookkeeper.increasebookkeeper.parser.model.{Currency, Discount, Transaction}

package object model {

  case class Payment(id: String,
                     clientId: String,
                     date: LocalDate,
                     currency: Currency,
                     totalAmount: BigDecimal,
                     totalDiscounts: BigDecimal,
                     totalWithDiscounts: BigDecimal,
                     transactions: List[Transaction]  ,
                     discounts: List[Discount])
}
