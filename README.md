<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
</head>
<body>
<h4>How to setup the DB</h4>
<pre>
CREATE TABLE IF NOT EXISTS `playerList` (
    `uuid` VARCHAR(255) NOT NULL,
    `id` VARCHAR(255) NOT NULL,
    `col3` TINYINT(1) NOT NULL DEFAULT 0,
    `publicCol` TINYINT(1) NOT NULL DEFAULT 0,
    `isChat` TINYINT(2) NOT NULL DEFAULT 0,
    PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
</pre>
</body>
</html>
