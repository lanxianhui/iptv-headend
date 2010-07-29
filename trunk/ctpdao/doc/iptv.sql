--
-- This is a template for the Database for use with the system.
-- Database Structure Version 1.0
-- ------------------------------------------------------------
--

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Baza danych: `iptv`
--

-- --------------------------------------------------------

--
-- Struktura tabeli dla  `Invite`
--

CREATE TABLE IF NOT EXISTS `Invite` (
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `code` varchar(255) collate utf8_polish_ci NOT NULL,
  `createdOn` datetime NOT NULL,
  `userId` bigint(20) unsigned default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci AUTO_INCREMENT=2 ;

-- --------------------------------------------------------

--
-- Struktura tabeli dla  `News`
--

CREATE TABLE IF NOT EXISTS `News` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `title` varchar(255) collate utf8_polish_ci NOT NULL,
  `body` text collate utf8_polish_ci NOT NULL,
  `createdOn` datetime NOT NULL,
  `editedOn` datetime NOT NULL,
  `userId` bigint(20) unsigned NOT NULL,
  `enabled` enum('TRUE','FALSE') collate utf8_polish_ci NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktura tabeli dla  `Program`
--

CREATE TABLE IF NOT EXISTS `Program` (
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `tvChannelId` bigint(20) unsigned NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text,
  `begin` timestamp NOT NULL default '0000-00-00 00:00:00',
  `end` timestamp NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=32431 ;

-- --------------------------------------------------------

--
-- Struktura tabeli dla  `Recording`
--

CREATE TABLE IF NOT EXISTS `Recording` (
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `programId` bigint(20) unsigned NOT NULL,
  `mode` enum('WAITING','PROCESSING','AVAILABLE','UNAVAILABLE') NOT NULL default 'WAITING',
  `fileName` varchar(255) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `PROGRAMID` (`programId`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=32431 ;

-- --------------------------------------------------------

--
-- Struktura tabeli dla  `TvChannel`
--

CREATE TABLE IF NOT EXISTS `TvChannel` (
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `name` varchar(128) NOT NULL,
  `ipAdress` varchar(15) NOT NULL,
  `port` int(11) NOT NULL,
  `unicastUrl` varchar(255) default NULL,
  `lcn` int(10) unsigned NOT NULL default '0',
  `icon` varchar(255) default NULL,
  `preRoll` int(11) NOT NULL default '0',
  `postRoll` int(11) NOT NULL default '0',
  `enabled` enum('TRUE','FALSE') NOT NULL default 'TRUE',
  PRIMARY KEY  (`id`),
  KEY `lcn` (`lcn`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=34 ;

-- --------------------------------------------------------

--
-- Struktura tabeli dla  `User`
--

CREATE TABLE IF NOT EXISTS `User` (
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `userName` varchar(255) character set latin1 NOT NULL,
  `password` varchar(255) character set latin1 default NULL,
  `authToken` varchar(255) character set latin1 default NULL,
  `quota` int(11) NOT NULL,
  `enabled` enum('TRUE','FALSE') character set latin1 NOT NULL default 'FALSE',
  `fullName` varchar(255) character set latin1 NOT NULL,
  `createdOn` datetime NOT NULL,
  `lastLogin` datetime default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=6 ;

-- --------------------------------------------------------

--
-- Struktura tabeli dla  `UserRecording`
--

CREATE TABLE IF NOT EXISTS `UserRecording` (
  `recordingId` bigint(20) unsigned NOT NULL,
  `userId` bigint(20) unsigned NOT NULL,
  PRIMARY KEY  (`recordingId`,`userId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
