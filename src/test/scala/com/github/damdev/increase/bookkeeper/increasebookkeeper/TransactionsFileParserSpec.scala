package com.github.damdev.increase.bookkeeper.increasebookkeeper

import java.time.LocalDate

import com.github.damdev.increase.bookkeeper.increasebookkeeper.parser.TransactionsFileParser
import com.github.damdev.increase.bookkeeper.increasebookkeeper.parser.model._
import org.specs2._

class TransactionsFileParserSpec extends Specification {
  def is =
    s2"""
 The 'TransactionsFileParser' should
   parse header ok                                               $okHeader
   parse transaction ok                                          $okTransaction
   parse discount ok                                             $okDiscount
   parse footer ok                                               $okFooter
   parse invalid record `type`                                   $invalidRecordType
   parse invalid amount                                          $invalidAmount
                                                                 """

  def okHeader = {
    val r = TransactionsFileParser.parse("12cfd15c1a578422ea337505c62517894   000000014842612900000020175720000146408557")
    r must beRight(Header("2cfd15c1a578422ea337505c62517894", ARS, 1484261.29, 20175.72, 1464085.57))
  }

  def okTransaction = {
    val r = TransactionsFileParser.parse("273f97bdd75a84646aa1d3fd5d9be77250000008879893     1")
    r must beRight(Transaction("73f97bdd75a84646aa1d3fd5d9be7725",88798.93,Accepted))
  }

  def okDiscount = {
    val r = TransactionsFileParser.parse("3d100dac922c948b0ab95bf87dd2e275a0000000189257   0")
    r must beRight(Discount("d100dac922c948b0ab95bf87dd2e275a",1892.57,IVA))
  }

  def okFooter = {
    val r = TransactionsFileParser.parse("4               201911169531137e967a4b24b5c596dcc77b9c5c")
    r must beRight(Footer(LocalDate.of(2019,11,16), "9531137e967a4b24b5c596dcc77b9c5c"))
  }

  def invalidRecordType = {
    val r = TransactionsFileParser.parse("02cfd15c1a578422ea337505c62517894   000000014842612900000020175720000146408557")
    r must beLeft("Error Invalid record type for line 02cfd15c1a578422ea337505c62517894   000000014842612900000020175720000146408557")
  }
  def invalidAmount = {
    val r = TransactionsFileParser.parse("273f97bdd75a84646aa1d3fd5d9be77250000a08879893     1")
    r must beLeft("Error Failure reading:digit for line 273f97bdd75a84646aa1d3fd5d9be77250000a08879893     1")
  }
}
