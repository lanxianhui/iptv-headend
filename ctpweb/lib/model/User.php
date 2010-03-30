<?php


 /**
  * User Value Object.
  * This class is value object representing database table User
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




class User {

    /** 
     * Persistent Instance variables. This data is directly 
     * mapped to the columns of database table.
     */
    var $id;
    var $userName;
    var $password;
    var $authToken;
    var $quota;
    var $enabled;
    var $fullName;
    var $createdOn;
    var $lastLogin;



    /** 
     * Constructors. DaoGen generates two constructors by default.
     * The first one takes no arguments and provides the most simple
     * way to create object instance. The another one takes one
     * argument, which is the primary key of the corresponding table.
     */

    function User () {

    }

    /* function User ($idIn) {

          $this->id = $idIn;

    } */


    /** 
     * Get- and Set-methods for persistent variables. The default
     * behaviour does not make any checks against malformed data,
     * so these might require some manual additions.
     */

    function getId() {
          return $this->id;
    }
    function setId($idIn) {
          $this->id = $idIn;
    }

    function getUserName() {
          return $this->userName;
    }
    function setUserName($userNameIn) {
          $this->userName = $userNameIn;
    }

    function getPassword() {
          return $this->password;
    }
    function setPassword($passwordIn) {
          $this->password = $passwordIn;
    }

    function getAuthToken() {
          return $this->authToken;
    }
    function setAuthToken($authTokenIn) {
          $this->authToken = $authTokenIn;
    }

    function getQuota() {
          return $this->quota;
    }
    function setQuota($quotaIn) {
          $this->quota = $quotaIn;
    }

    function getEnabled() {
          return $this->enabled;
    }
    function setEnabled($enabledIn) {
          $this->enabled = $enabledIn;
    }

    function getFullName() {
          return $this->fullName;
    }
    function setFullName($fullNameIn) {
          $this->fullName = $fullNameIn;
    }

    function getCreatedOn() {
          return $this->createdOn;
    }
    function setCreatedOn($createdOnIn) {
          $this->createdOn = $createdOnIn;
    }

    function getLastLogin() {
          return $this->lastLogin;
    }
    function setLastLogin($lastLoginIn) {
          $this->lastLogin = $lastLoginIn;
    }



    /** 
     * setAll allows to set all persistent variables in one method call.
     * This is useful, when all data is available and it is needed to 
     * set the initial state of this object. Note that this method will
     * directly modify instance variales, without going trough the 
     * individual set-methods.
     */

    function setAll($idIn,
          $userNameIn,
          $passwordIn,
          $authTokenIn,
          $quotaIn,
          $enabledIn,
          $fullNameIn,
          $createdOnIn,
          $lastLoginIn) {
          $this->id = $idIn;
          $this->userName = $userNameIn;
          $this->password = $passwordIn;
          $this->authToken = $authTokenIn;
          $this->quota = $quotaIn;
          $this->enabled = $enabledIn;
          $this->fullName = $fullNameIn;
          $this->createdOn = $createdOnIn;
          $this->lastLogin = $lastLoginIn;
    }


    /** 
     * hasEqualMapping-method will compare two User instances
     * and return true if they contain same values in all persistent instance 
     * variables. If hasEqualMapping returns true, it does not mean the objects
     * are the same instance. However it does mean that in that moment, they 
     * are mapped to the same row in database.
     */
    function hasEqualMapping($valueObject) {

          if ($valueObject->getId() != $this->id) {
                    return(false);
          }
          if ($valueObject->getUserName() != $this->userName) {
                    return(false);
          }
          if ($valueObject->getPassword() != $this->password) {
                    return(false);
          }
          if ($valueObject->getAuthToken() != $this->authToken) {
                    return(false);
          }
          if ($valueObject->getQuota() != $this->quota) {
                    return(false);
          }
          if ($valueObject->getEnabled() != $this->enabled) {
                    return(false);
          }
          if ($valueObject->getFullName() != $this->fullName) {
                    return(false);
          }
          if ($valueObject->getCreatedOn() != $this->createdOn) {
                    return(false);
          }
          if ($valueObject->getLastLogin() != $this->lastLogin) {
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
        $out = $out."\nclass User, mapping to table User\n";
        $out = $out."Persistent attributes: \n"; 
        $out = $out."id = ".$this->id."\n"; 
        $out = $out."userName = ".$this->userName."\n"; 
        $out = $out."password = ".$this->password."\n"; 
        $out = $out."authToken = ".$this->authToken."\n"; 
        $out = $out."quota = ".$this->quota."\n"; 
        $out = $out."enabled = ".$this->enabled."\n"; 
        $out = $out."fullName = ".$this->fullName."\n"; 
        $out = $out."createdOn = ".$this->createdOn."\n"; 
        $out = $out."lastLogin = ".$this->lastLogin."\n"; 
        return $out;
    }


    /**
     * Clone will return identical deep copy of this valueObject.
     * Note, that this method is different than the clone() which
     * is defined in java.lang.Object. Here, the retuned cloned object
     * will also have all its attributes cloned.
     */
    function __clone() {
        $cloned = new User();

        $cloned->setId($this->id); 
        $cloned->setUserName($this->userName); 
        $cloned->setPassword($this->password); 
        $cloned->setAuthToken($this->authToken); 
        $cloned->setQuota($this->quota); 
        $cloned->setEnabled($this->enabled); 
        $cloned->setFullName($this->fullName); 
        $cloned->setCreatedOn($this->createdOn); 
        $cloned->setLastLogin($this->lastLogin); 

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