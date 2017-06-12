val s = "ABC.12.123"
val firstIndexOfDot = s.indexOf(".")
val esType = s.substring(0, firstIndexOfDot)
val key = s.substring(firstIndexOfDot + 1)