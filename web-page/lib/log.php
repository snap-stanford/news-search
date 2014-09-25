<?php
/**
 * Razred za delo z dnevniskimi datotekami
 * @date: 1.10.2008
 * @author: Andrej Krevl
 */

class Log {
  var $activity_log;
  var $error_log;
  
  /**
   * Pripravi razred za delo z dnevniki
   * @param $factivity Dnevniska datoteka aktivnosti
   * @param $ferror Dnevniska datoteka napak (opcijsko, ce izpustis, gredo vsi 
   *                zapisi v $factivity
   */
  function Log($factivity, $ferror = NULL) {
    $this->activity_log = $factivity;
    if ($ferror == NULL) {
      $this->error_log = $factivity;
    } else {
      $this->error_log = $ferror;
    }
  }
  
  /**
   * @return Trenutni datum in cas (01.01.1970 13:45:10)
   */
  function getTimestamp() {
    return date('d.m.Y G:i:s');
  }
  
  /**
   * Zapisi sporocilo o napaki v ERROR log
   *
   * @param $message Sporocilo (Povezava z MS SQL ni uspela)
   */
  function error($message) {
    file_put_contents($this->error_log, $this->getTimestamp() . " ERROR: $message\n", FILE_APPEND);
  }
  
  /**
   * Zapisi informativno sporocilo v ACTIVITY log
   *
   * @param $message Sporocilo (Uspesno povezan z MS SQL)
   */
  function activity($message) {
    file_put_contents($this->activity_log, $this->getTimestamp() . " OK: $message\n", FILE_APPEND);
  }
}
?>