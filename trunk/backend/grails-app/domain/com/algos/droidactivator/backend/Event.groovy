package com.algos.droidactivator.backend

class Event {

  // domain properties
  def Activation activation // parent record
  def long timestamp  // event timestamp
  def int code  // event code
  def String details  // event details

  static constraints = {
  }

}
