<?php


 /**
  * UserRecording Value Object.
  * This class is value object representing database table UserRecording
  * This class is intented to be used together with associated Dao object.
  */

 /**
  * This sourcecode has been generated by FREE DaoGen generator version 2.4.1.
  * The usage of generated code is restricted to OpenSource software projects
  * only. DaoGen is available in http://titaniclinux.net/daogen/
  * It has been programmed by Tuomo Lukka, Tuomo.Lukka@iki.fi
  *
  * DaoGen license: The following DaoGen generated source code is licensed
  * under the terms of GNU GPL license. The full text for license is available
  * in GNU project's pages: http://www.gnu.org/copyleft/gpl.html
  *
  * If you wish to use the DaoGen generator to produce code for closed-source
  * commercial applications, you must pay the lisence fee. The price is
  * 5 USD or 5 Eur for each database table, you are generating code for.
  * (That includes unlimited amount of iterations with all supported languages
  * for each database table you are paying for.) Send mail to
  * "Tuomo.Lukka@iki.fi" for more information. Thank you!
  */




class UserRecording {

    /** 
     * Persistent Instance variables. This data is directly 
     * mapped to the columns of database table.
     */
    var $recordingId;
    var $userId;



    /** 
     * Constructors. DaoGen generates two constructors by default.
     * The first one takes no arguments and provides the most simple
     * way to create object instance. The another one takes one
     * argument, which is the primary key of the corresponding table.
     */

    function UserRecording () {

    }

    /* function UserRecording ($recordingIdIn, $userIdIn) {

          $this->recordingId = $recordingIdIn;
          $this->userId = $userIdIn;

    } */


    /** 
     * Get- and Set-methods for persistent variables. The default
     * behaviour does not make any checks against malformed data,
     * so these might require some manual additions.
     */

    function getRecordingId() {
          return $this->recordingId;
    }
    function setRecordingId($recordingIdIn) {
          $this->recordingId = $recordingIdIn;
    }

    function getUserId() {
          return $this->userId;
    }
    function setUserId($userIdIn) {
          $this->userId = $userIdIn;
    }



    /** 
     * setAll allows to set all persistent variables in one method call.
     * This is useful, when all data is available and it is needed to 
     * set the initial state of this object. Note that this method will
     * directly modify instance variales, without going trough the 
     * individual set-methods.
     */

    function setAll($recordingIdIn,
          $userIdIn) {
          $this->recordingId = $recordingIdIn;
          $this->userId = $userIdIn;
    }


    /** 
     * hasEqualMapping-method will compare two UserRecording instances
     * and return true if they contain same values in all persistent instance 
     * variables. If hasEqualMapping returns true, it does not mean the objects
     * are the same instance. However it does mean that in that moment, they 
     * are mapped to the same row in database.
     */
    function hasEqualMapping($valueObject) {

          if ($valueObject->getRecordingId() != $this->recordingId) {
                    return(false);
          }
          if ($valueObject->getUserId() != $this->userId) {
                    return(false);
          }

          return true;
    }



    /**
     * toString will return String object representing the state of this 
     * valueObject. This is useful during application development, and 
     * possibly when application is writing object states in textlog.
     */
    function __toString() {
        $out = $this->getDaogenVersion();
        $out = $out."\nclass UserRecording, mapping to table UserRecording\n";
        $out = $out."Persistent attributes: \n"; 
        $out = $out."recordingId = ".$this->recordingId."\n"; 
        $out = $out."userId = ".$this->userId."\n"; 
        return $out;
    }


    /**
     * Clone will return identical deep copy of this valueObject.
     * Note, that this method is different than the clone() which
     * is defined in java.lang.Object. Here, the retuned cloned object
     * will also have all its attributes cloned.
     */
    function __clone() {
        $cloned = new UserRecording();

        $cloned->setRecordingId($this->recordingId); 
        $cloned->setUserId($this->userId); 

        return $cloned;
    }



    /** 
     * getDaogenVersion will return information about
     * generator which created these sources.
     */
    function getDaogenVersion() {
        return "DaoGen version 2.4.1";
    }

}

?>