package com.github.david04.liftutils.fontawesome

import scala.xml.Unparsed


/**
 * 3.2.1
 */
object Icon extends Enumeration {
  type Icon = Value

  //cat /tmp/icon | cut -d " " -f 1|while read i ; do echo "val $(echo "$i"|sed 's/icon-//g'|sed 's/-/_/g') = Value(\"$i\")"; done|sort
  val adjust = Value("icon-adjust")
  val adn = Value("icon-adn")
  val align_center = Value("icon-align-center")
  val align_justify = Value("icon-align-justify")
  val align_left = Value("icon-align-left")
  val align_right = Value("icon-align-right")
  val ambulance = Value("icon-ambulance")
  val anchor = Value("icon-anchor")
  val android = Value("icon-android")
  val angle_down = Value("icon-angle-down")
  val angle_left = Value("icon-angle-left")
  val angle_right = Value("icon-angle-right")
  val angle_up = Value("icon-angle-up")
  val apple = Value("icon-apple")
  val archive = Value("icon-archive")
  val arrow_down = Value("icon-arrow-down")
  val arrow_left = Value("icon-arrow-left")
  val arrow_right = Value("icon-arrow-right")
  val arrow_up = Value("icon-arrow-up")
  val asterisk = Value("icon-asterisk")
  val backward = Value("icon-backward")
  val ban_circle = Value("icon-ban-circle")
  val bar_chart = Value("icon-bar-chart")
  val barcode = Value("icon-barcode")
  val beaker = Value("icon-beaker")
  val beer = Value("icon-beer")
  val bell_alt = Value("icon-bell-alt")
  val bell = Value("icon-bell")
  val bitbucket_sign = Value("icon-bitbucket-sign")
  val bitbucket = Value("icon-bitbucket")
  val bold = Value("icon-bold")
  val bolt = Value("icon-bolt")
  val bookmark_empty = Value("icon-bookmark-empty")
  val bookmark = Value("icon-bookmark")
  val book = Value("icon-book")
  val briefcase = Value("icon-briefcase")
  val btc = Value("icon-btc")
  val bug = Value("icon-bug")
  val building = Value("icon-building")
  val bullhorn = Value("icon-bullhorn")
  val bullseye = Value("icon-bullseye")
  val calendar_empty = Value("icon-calendar-empty")
  val calendar = Value("icon-calendar")
  val camera_retro = Value("icon-camera-retro")
  val camera = Value("icon-camera")
  val caret_down = Value("icon-caret-down")
  val caret_left = Value("icon-caret-left")
  val caret_right = Value("icon-caret-right")
  val caret_up = Value("icon-caret-up")
  val certificate = Value("icon-certificate")
  val check_empty = Value("icon-check-empty")
  val check_minus = Value("icon-check-minus")
  val check_sign = Value("icon-check-sign")
  val check = Value("icon-check")
  val chevron_down = Value("icon-chevron-down")
  val chevron_left = Value("icon-chevron-left")
  val chevron_right = Value("icon-chevron-right")
  val chevron_sign_down = Value("icon-chevron-sign-down")
  val chevron_sign_left = Value("icon-chevron-sign-left")
  val chevron_sign_right = Value("icon-chevron-sign-right")
  val chevron_sign_up = Value("icon-chevron-sign-up")
  val chevron_up = Value("icon-chevron-up")
  val circle_arrow_down = Value("icon-circle-arrow-down")
  val circle_arrow_left = Value("icon-circle-arrow-left")
  val circle_arrow_right = Value("icon-circle-arrow-right")
  val circle_arrow_up = Value("icon-circle-arrow-up")
  val circle_blank = Value("icon-circle-blank")
  val circle = Value("icon-circle")
  val cloud_download = Value("icon-cloud-download")
  val cloud_upload = Value("icon-cloud-upload")
  val cloud = Value("icon-cloud")
  val cny = Value("icon-cny")
  val code_fork = Value("icon-code-fork")
  val code = Value("icon-code")
  val coffee = Value("icon-coffee")
  val cogs = Value("icon-cogs")
  val cog = Value("icon-cog")
  val collapse_alt = Value("icon-collapse-alt")
  val collapse_top = Value("icon-collapse-top")
  val collapse = Value("icon-collapse")
  val columns = Value("icon-columns")
  val comment_alt = Value("icon-comment-alt")
  val comments_alt = Value("icon-comments-alt")
  val comments = Value("icon-comments")
  val comment = Value("icon-comment")
  val compass = Value("icon-compass")
  val copy = Value("icon-copy")
  val credit_card = Value("icon-credit-card")
  val crop = Value("icon-crop")
  val css3 = Value("icon-css3")
  val cut = Value("icon-cut")
  val dashboard = Value("icon-dashboard")
  val desktop = Value("icon-desktop")
  val double_angle_down = Value("icon-double-angle-down")
  val double_angle_left = Value("icon-double-angle-left")
  val double_angle_right = Value("icon-double-angle-right")
  val double_angle_up = Value("icon-double-angle-up")
  val download_alt = Value("icon-download-alt")
  val download = Value("icon-download")
  val dribbble = Value("icon-dribbble")
  val dropbox = Value("icon-dropbox")
  val edit_sign = Value("icon-edit-sign")
  val edit = Value("icon-edit")
  val eject = Value("icon-eject")
  val ellipsis_horizontal = Value("icon-ellipsis-horizontal")
  val ellipsis_vertical = Value("icon-ellipsis-vertical")
  val envelope_alt = Value("icon-envelope-alt")
  val envelope = Value("icon-envelope")
  val eraser = Value("icon-eraser")
  val eur = Value("icon-eur")
  val exchange = Value("icon-exchange")
  val exclamation_sign = Value("icon-exclamation-sign")
  val exclamation = Value("icon-exclamation")
  val expand_alt = Value("icon-expand-alt")
  val expand = Value("icon-expand")
  val external_link_sign = Value("icon-external-link-sign")
  val external_link = Value("icon-external-link")
  val eye_close = Value("icon-eye-close")
  val eye_open = Value("icon-eye-open")
  val facebook_sign = Value("icon-facebook-sign")
  val facebook = Value("icon-facebook")
  val facetime_video = Value("icon-facetime-video")
  val fast_backward = Value("icon-fast-backward")
  val fast_forward = Value("icon-fast-forward")
  val female = Value("icon-female")
  val fighter_jet = Value("icon-fighter-jet")
  val file_alt = Value("icon-file-alt")
  val file_text_alt = Value("icon-file-text-alt")
  val file_text = Value("icon-file-text")
  val file = Value("icon-file")
  val film = Value("icon-film")
  val filter = Value("icon-filter")
  val fire_extinguisher = Value("icon-fire-extinguisher")
  val fire = Value("icon-fire")
  val flag_alt = Value("icon-flag-alt")
  val flag_checkered = Value("icon-flag-checkered")
  val flag = Value("icon-flag")
  val flickr = Value("icon-flickr")
  val folder_close_alt = Value("icon-folder-close-alt")
  val folder_close = Value("icon-folder-close")
  val folder_open_alt = Value("icon-folder-open-alt")
  val folder_open = Value("icon-folder-open")
  val font = Value("icon-font")
  val food = Value("icon-food")
  val forward = Value("icon-forward")
  val foursquare = Value("icon-foursquare")
  val frown = Value("icon-frown")
  val fullscreen = Value("icon-fullscreen")
  val gamepad = Value("icon-gamepad")
  val gbp = Value("icon-gbp")
  val gift = Value("icon-gift")
  val github_alt = Value("icon-github-alt")
  val github_sign = Value("icon-github-sign")
  val github = Value("icon-github")
  val gittip = Value("icon-gittip")
  val glass = Value("icon-glass")
  val globe = Value("icon-globe")
  val google_plus_sign = Value("icon-google-plus-sign")
  val google_plus = Value("icon-google-plus")
  val group = Value("icon-group")
  val hand_down = Value("icon-hand-down")
  val hand_left = Value("icon-hand-left")
  val hand_right = Value("icon-hand-right")
  val hand_up = Value("icon-hand-up")
  val hdd = Value("icon-hdd")
  val headphones = Value("icon-headphones")
  val heart_empty = Value("icon-heart-empty")
  val heart = Value("icon-heart")
  val home = Value("icon-home")
  val hospital = Value("icon-hospital")
  val h_sign = Value("icon-h-sign")
  val html5 = Value("icon-html5")
  val inbox = Value("icon-inbox")
  val indent_left = Value("icon-indent-left")
  val indent_right = Value("icon-indent-right")
  val info_sign = Value("icon-info-sign")
  val info = Value("icon-info")
  val inr = Value("icon-inr")
  val instagram = Value("icon-instagram")
  val italic = Value("icon-italic")
  val jpy = Value("icon-jpy")
  val keyboard = Value("icon-keyboard")
  val key = Value("icon-key")
  val krw = Value("icon-krw")
  val laptop = Value("icon-laptop")
  val leaf = Value("icon-leaf")
  val legal = Value("icon-legal")
  val lemon = Value("icon-lemon")
  val level_down = Value("icon-level-down")
  val level_up = Value("icon-level-up")
  val lightbulb = Value("icon-lightbulb")
  val linkedin_sign = Value("icon-linkedin-sign")
  val linkedin = Value("icon-linkedin")
  val link = Value("icon-link")
  val linux = Value("icon-linux")
  val list_alt = Value("icon-list-alt")
  val list_ol = Value("icon-list-ol")
  val list_ul = Value("icon-list-ul")
  val list = Value("icon-list")
  val location_arrow = Value("icon-location-arrow")
  val lock = Value("icon-lock")
  val long_arrow_down = Value("icon-long-arrow-down")
  val long_arrow_left = Value("icon-long-arrow-left")
  val long_arrow_right = Value("icon-long-arrow-right")
  val long_arrow_up = Value("icon-long-arrow-up")
  val magic = Value("icon-magic")
  val magnet = Value("icon-magnet")
  val mail_reply_all = Value("icon-mail-reply-all")
  val male = Value("icon-male")
  val map_marker = Value("icon-map-marker")
  val maxcdn = Value("icon-maxcdn")
  val medkit = Value("icon-medkit")
  val meh = Value("icon-meh")
  val microphone_off = Value("icon-microphone-off")
  val microphone = Value("icon-microphone")
  val minus_sign_alt = Value("icon-minus-sign-alt")
  val minus_sign = Value("icon-minus-sign")
  val minus = Value("icon-minus")
  val mobile_phone = Value("icon-mobile-phone")
  val money = Value("icon-money")
  val moon = Value("icon-moon")
  val move = Value("icon-move")
  val music = Value("icon-music")
  val off = Value("icon-off")
  val ok_circle = Value("icon-ok-circle")
  val ok_sign = Value("icon-ok-sign")
  val ok = Value("icon-ok")
  val paper_clip = Value("icon-paper-clip")
  val paste = Value("icon-paste")
  val pause = Value("icon-pause")
  val pencil = Value("icon-pencil")
  val phone_sign = Value("icon-phone-sign")
  val phone = Value("icon-phone")
  val picture = Value("icon-picture")
  val pinterest_sign = Value("icon-pinterest-sign")
  val pinterest = Value("icon-pinterest")
  val plane = Value("icon-plane")
  val play_circle = Value("icon-play-circle")
  val play_sign = Value("icon-play-sign")
  val play = Value("icon-play")
  val plus_sign_alt = Value("icon-plus-sign-alt")
  val plus_sign = Value("icon-plus-sign")
  val plus = Value("icon-plus")
  val print = Value("icon-print")
  val pushpin = Value("icon-pushpin")
  val puzzle_piece = Value("icon-puzzle-piece")
  val qrcode = Value("icon-qrcode")
  val question_sign = Value("icon-question-sign")
  val question = Value("icon-question")
  val quote_left = Value("icon-quote-left")
  val quote_right = Value("icon-quote-right")
  val random = Value("icon-random")
  val refresh = Value("icon-refresh")
  val remove_circle = Value("icon-remove-circle")
  val remove_sign = Value("icon-remove-sign")
  val remove = Value("icon-remove")
  val renren = Value("icon-renren")
  val reorder = Value("icon-reorder")
  val repeat = Value("icon-repeat")
  val reply_all = Value("icon-reply-all")
  val reply = Value("icon-reply")
  val resize_full = Value("icon-resize-full")
  val resize_horizontal = Value("icon-resize-horizontal")
  val resize_small = Value("icon-resize-small")
  val resize_vertical = Value("icon-resize-vertical")
  val retweet = Value("icon-retweet")
  val road = Value("icon-road")
  val rocket = Value("icon-rocket")
  val rss_sign = Value("icon-rss-sign")
  val rss = Value("icon-rss")
  val save = Value("icon-save")
  val screenshot = Value("icon-screenshot")
  val search = Value("icon-search")
  val share_alt = Value("icon-share-alt")
  val share_sign = Value("icon-share-sign")
  val share = Value("icon-share")
  val shield = Value("icon-shield")
  val shopping_cart = Value("icon-shopping-cart")
  val signal = Value("icon-signal")
  val sign_blank = Value("icon-sign-blank")
  val signin = Value("icon-signin")
  val signout = Value("icon-signout")
  val sitemap = Value("icon-sitemap")
  val skype = Value("icon-skype")
  val smile = Value("icon-smile")
  val sort_by_alphabet_alt = Value("icon-sort-by-alphabet-alt")
  val sort_by_alphabet = Value("icon-sort-by-alphabet")
  val sort_by_attributes_alt = Value("icon-sort-by-attributes-alt")
  val sort_by_attributes = Value("icon-sort-by-attributes")
  val sort_by_order_alt = Value("icon-sort-by-order-alt")
  val sort_by_order = Value("icon-sort-by-order")
  val sort_down = Value("icon-sort-down")
  val sort_up = Value("icon-sort-up")
  val sort = Value("icon-sort")
  val spinner = Value("icon-spinner")
  val stackexchange = Value("icon-stackexchange")
  val star_empty = Value("icon-star-empty")
  val star_half_empty = Value("icon-star-half-empty")
  val star_half = Value("icon-star-half")
  val star = Value("icon-star")
  val step_backward = Value("icon-step-backward")
  val step_forward = Value("icon-step-forward")
  val stethoscope = Value("icon-stethoscope")
  val stop = Value("icon-stop")
  val strikethrough = Value("icon-strikethrough")
  val subscript = Value("icon-subscript")
  val suitcase = Value("icon-suitcase")
  val sun = Value("icon-sun")
  val superscript = Value("icon-superscript")
  val tablet = Value("icon-tablet")
  val table = Value("icon-table")
  val tags = Value("icon-tags")
  val tag = Value("icon-tag")
  val tasks = Value("icon-tasks")
  val terminal = Value("icon-terminal")
  val text_height = Value("icon-text-height")
  val text_width = Value("icon-text-width")
  val th_large = Value("icon-th-large")
  val th_list = Value("icon-th-list")
  val thumbs_down_alt = Value("icon-thumbs-down-alt")
  val thumbs_down = Value("icon-thumbs-down")
  val thumbs_up_alt = Value("icon-thumbs-up-alt")
  val thumbs_up = Value("icon-thumbs-up")
  val th = Value("icon-th")
  val ticket = Value("icon-ticket")
  val time = Value("icon-time")
  val tint = Value("icon-tint")
  val trash = Value("icon-trash")
  val trello = Value("icon-trello")
  val trophy = Value("icon-trophy")
  val truck = Value("icon-truck")
  val tumblr_sign = Value("icon-tumblr-sign")
  val tumblr = Value("icon-tumblr")
  val twitter_sign = Value("icon-twitter-sign")
  val twitter = Value("icon-twitter")
  val umbrella = Value("icon-umbrella")
  val underline = Value("icon-underline")
  val undo = Value("icon-undo")
  val unlink = Value("icon-unlink")
  val unlock_alt = Value("icon-unlock-alt")
  val unlock = Value("icon-unlock")
  val upload_alt = Value("icon-upload-alt")
  val upload = Value("icon-upload")
  val usd = Value("icon-usd")
  val user_md = Value("icon-user-md")
  val user = Value("icon-user")
  val vk = Value("icon-vk")
  val volume_down = Value("icon-volume-down")
  val volume_off = Value("icon-volume-off")
  val volume_up = Value("icon-volume-up")
  val warning_sign = Value("icon-warning-sign")
  val weibo = Value("icon-weibo")
  val windows = Value("icon-windows")
  val wrench = Value("icon-wrench")
  val xing_sign = Value("icon-xing-sign")
  val xing = Value("icon-xing")
  val youtube_play = Value("icon-youtube-play")
  val youtube_sign = Value("icon-youtube-sign")
  val youtube = Value("icon-youtube")
  val zoom_in = Value("icon-zoom-in")
  val zoom_out = Value("icon-zoom-out")

  //cat /tmp/icon |while read i; do echo "\"$(echo $i|cut -d " " -f 1)\" -> \"$(echo $i | replace "#x" "|" | cut -d "|" -f 2 | cut -d ";" -f 1)\","; done|sort
  val unicode =
    Map(
      "icon-adjust" -> "f042",
      "icon-adn" -> "f170",
      "icon-align-center" -> "f037",
      "icon-align-justify" -> "f039",
      "icon-align-left" -> "f036",
      "icon-align-right" -> "f038",
      "icon-ambulance" -> "f0f9",
      "icon-anchor" -> "f13d",
      "icon-android" -> "f17b",
      "icon-angle-down" -> "f107",
      "icon-angle-left" -> "f104",
      "icon-angle-right" -> "f105",
      "icon-angle-up" -> "f106",
      "icon-apple" -> "f179",
      "icon-archive" -> "f187",
      "icon-arrow-down" -> "f063",
      "icon-arrow-left" -> "f060",
      "icon-arrow-right" -> "f061",
      "icon-arrow-up" -> "f062",
      "icon-asterisk" -> "f069",
      "icon-backward" -> "f04a",
      "icon-ban-circle" -> "f05e",
      "icon-bar-chart" -> "f080",
      "icon-barcode" -> "f02a",
      "icon-beaker" -> "f0c3",
      "icon-beer" -> "f0fc",
      "icon-bell-alt" -> "f0f3",
      "icon-bell" -> "f0a2",
      "icon-bitbucket" -> "f171",
      "icon-bitbucket-sign" -> "f172",
      "icon-bold" -> "f032",
      "icon-bolt" -> "f0e7",
      "icon-book" -> "f02d",
      "icon-bookmark-empty" -> "f097",
      "icon-bookmark" -> "f02e",
      "icon-briefcase" -> "f0b1",
      "icon-btc" -> "f15a",
      "icon-bug" -> "f188",
      "icon-building" -> "f0f7",
      "icon-bullhorn" -> "f0a1",
      "icon-bullseye" -> "f140",
      "icon-calendar-empty" -> "f133",
      "icon-calendar" -> "f073",
      "icon-camera" -> "f030",
      "icon-camera-retro" -> "f083",
      "icon-caret-down" -> "f0d7",
      "icon-caret-left" -> "f0d9",
      "icon-caret-right" -> "f0da",
      "icon-caret-up" -> "f0d8",
      "icon-certificate" -> "f0a3",
      "icon-check-empty" -> "f096",
      "icon-check" -> "f046",
      "icon-check-minus" -> "f147",
      "icon-check-sign" -> "f14a",
      "icon-chevron-down" -> "f078",
      "icon-chevron-left" -> "f053",
      "icon-chevron-right" -> "f054",
      "icon-chevron-sign-down" -> "f13a",
      "icon-chevron-sign-left" -> "f137",
      "icon-chevron-sign-right" -> "f138",
      "icon-chevron-sign-up" -> "f139",
      "icon-chevron-up" -> "f077",
      "icon-circle-arrow-down" -> "f0ab",
      "icon-circle-arrow-left" -> "f0a8",
      "icon-circle-arrow-right" -> "f0a9",
      "icon-circle-arrow-up" -> "f0aa",
      "icon-circle-blank" -> "f10c",
      "icon-circle" -> "f111",
      "icon-cloud-download" -> "f0ed",
      "icon-cloud" -> "f0c2",
      "icon-cloud-upload" -> "f0ee",
      "icon-cny" -> "f158",
      "icon-code" -> "f121",
      "icon-code-fork" -> "f126",
      "icon-coffee" -> "f0f4",
      "icon-cog" -> "f013",
      "icon-cogs" -> "f085",
      "icon-collapse-alt" -> "f117",
      "icon-collapse" -> "f150",
      "icon-collapse-top" -> "f151",
      "icon-columns" -> "f0db",
      "icon-comment-alt" -> "f0e5",
      "icon-comment" -> "f075",
      "icon-comments-alt" -> "f0e6",
      "icon-comments" -> "f086",
      "icon-compass" -> "f14e",
      "icon-copy" -> "f0c5",
      "icon-credit-card" -> "f09d",
      "icon-crop" -> "f125",
      "icon-css3" -> "f13c",
      "icon-cut" -> "f0c4",
      "icon-dashboard" -> "f0e4",
      "icon-desktop" -> "f108",
      "icon-double-angle-down" -> "f103",
      "icon-double-angle-left" -> "f100",
      "icon-double-angle-right" -> "f101",
      "icon-double-angle-up" -> "f102",
      "icon-download-alt" -> "f019",
      "icon-download" -> "f01a",
      "icon-dribbble" -> "f17d",
      "icon-dropbox" -> "f16b",
      "icon-edit" -> "f044",
      "icon-edit-sign" -> "f14b",
      "icon-eject" -> "f052",
      "icon-ellipsis-horizontal" -> "f141",
      "icon-ellipsis-vertical" -> "f142",
      "icon-envelope-alt" -> "f003",
      "icon-envelope" -> "f0e0",
      "icon-eraser" -> "f12d",
      "icon-eur" -> "f153",
      "icon-exchange" -> "f0ec",
      "icon-exclamation" -> "f12a",
      "icon-exclamation-sign" -> "f06a",
      "icon-expand-alt" -> "f116",
      "icon-expand" -> "f152",
      "icon-external-link" -> "f08e",
      "icon-external-link-sign" -> "f14c",
      "icon-eye-close" -> "f070",
      "icon-eye-open" -> "f06e",
      "icon-facebook" -> "f09a",
      "icon-facebook-sign" -> "f082",
      "icon-facetime-video" -> "f03d",
      "icon-fast-backward" -> "f049",
      "icon-fast-forward" -> "f050",
      "icon-female" -> "f182",
      "icon-fighter-jet" -> "f0fb",
      "icon-file-alt" -> "f016",
      "icon-file" -> "f15b",
      "icon-file-text-alt" -> "f0f6",
      "icon-file-text" -> "f15c",
      "icon-film" -> "f008",
      "icon-filter" -> "f0b0",
      "icon-fire-extinguisher" -> "f134",
      "icon-fire" -> "f06d",
      "icon-flag-alt" -> "f11d",
      "icon-flag-checkered" -> "f11e",
      "icon-flag" -> "f024",
      "icon-flickr" -> "f16e",
      "icon-folder-close-alt" -> "f114",
      "icon-folder-close" -> "f07b",
      "icon-folder-open-alt" -> "f115",
      "icon-folder-open" -> "f07c",
      "icon-font" -> "f031",
      "icon-food" -> "f0f5",
      "icon-forward" -> "f04e",
      "icon-foursquare" -> "f180",
      "icon-frown" -> "f119",
      "icon-fullscreen" -> "f0b2",
      "icon-gamepad" -> "f11b",
      "icon-gbp" -> "f154",
      "icon-gift" -> "f06b",
      "icon-github-alt" -> "f113",
      "icon-github" -> "f09b",
      "icon-github-sign" -> "f092",
      "icon-gittip" -> "f184",
      "icon-glass" -> "f000",
      "icon-globe" -> "f0ac",
      "icon-google-plus" -> "f0d5",
      "icon-google-plus-sign" -> "f0d4",
      "icon-group" -> "f0c0",
      "icon-hand-down" -> "f0a7",
      "icon-hand-left" -> "f0a5",
      "icon-hand-right" -> "f0a4",
      "icon-hand-up" -> "f0a6",
      "icon-hdd" -> "f0a0",
      "icon-headphones" -> "f025",
      "icon-heart-empty" -> "f08a",
      "icon-heart" -> "f004",
      "icon-home" -> "f015",
      "icon-hospital" -> "f0f8",
      "icon-h-sign" -> "f0fd",
      "icon-html5" -> "f13b",
      "icon-inbox" -> "f01c",
      "icon-indent-left" -> "f03b",
      "icon-indent-right" -> "f03c",
      "icon-info" -> "f129",
      "icon-info-sign" -> "f05a",
      "icon-inr" -> "f156",
      "icon-instagram" -> "f16d",
      "icon-italic" -> "f033",
      "icon-jpy" -> "f157",
      "icon-keyboard" -> "f11c",
      "icon-key" -> "f084",
      "icon-krw" -> "f159",
      "icon-laptop" -> "f109",
      "icon-leaf" -> "f06c",
      "icon-legal" -> "f0e3",
      "icon-lemon" -> "f094",
      "icon-level-down" -> "f149",
      "icon-level-up" -> "f148",
      "icon-lightbulb" -> "f0eb",
      "icon-linkedin" -> "f0e1",
      "icon-linkedin-sign" -> "f08c",
      "icon-link" -> "f0c1",
      "icon-linux" -> "f17c",
      "icon-list-alt" -> "f022",
      "icon-list" -> "f03a",
      "icon-list-ol" -> "f0cb",
      "icon-list-ul" -> "f0ca",
      "icon-location-arrow" -> "f124",
      "icon-lock" -> "f023",
      "icon-long-arrow-down" -> "f175",
      "icon-long-arrow-left" -> "f177",
      "icon-long-arrow-right" -> "f178",
      "icon-long-arrow-up" -> "f176",
      "icon-magic" -> "f0d0",
      "icon-magnet" -> "f076",
      "icon-mail-reply-all" -> "f122",
      "icon-male" -> "f183",
      "icon-map-marker" -> "f041",
      "icon-maxcdn" -> "f136",
      "icon-medkit" -> "f0fa",
      "icon-meh" -> "f11a",
      "icon-microphone" -> "f130",
      "icon-microphone-off" -> "f131",
      "icon-minus" -> "f068",
      "icon-minus-sign-alt" -> "f146",
      "icon-minus-sign" -> "f056",
      "icon-mobile-phone" -> "f10b",
      "icon-money" -> "f0d6",
      "icon-moon" -> "f186",
      "icon-move" -> "f047",
      "icon-music" -> "f001",
      "icon-off" -> "f011",
      "icon-ok-circle" -> "f05d",
      "icon-ok" -> "f00c",
      "icon-ok-sign" -> "f058",
      "icon-paper-clip" -> "f0c6",
      "icon-paste" -> "f0ea",
      "icon-pause" -> "f04c",
      "icon-pencil" -> "f040",
      "icon-phone" -> "f095",
      "icon-phone-sign" -> "f098",
      "icon-picture" -> "f03e",
      "icon-pinterest" -> "f0d2",
      "icon-pinterest-sign" -> "f0d3",
      "icon-plane" -> "f072",
      "icon-play-circle" -> "f01d",
      "icon-play" -> "f04b",
      "icon-play-sign" -> "f144",
      "icon-plus" -> "f067",
      "icon-plus-sign-alt" -> "f0fe",
      "icon-plus-sign" -> "f055",
      "icon-print" -> "f02f",
      "icon-pushpin" -> "f08d",
      "icon-puzzle-piece" -> "f12e",
      "icon-qrcode" -> "f029",
      "icon-question" -> "f128",
      "icon-question-sign" -> "f059",
      "icon-quote-left" -> "f10d",
      "icon-quote-right" -> "f10e",
      "icon-random" -> "f074",
      "icon-refresh" -> "f021",
      "icon-remove-circle" -> "f05c",
      "icon-remove" -> "f00d",
      "icon-remove-sign" -> "f057",
      "icon-renren" -> "f18b",
      "icon-reorder" -> "f0c9",
      "icon-repeat" -> "f01e",
      "icon-reply-all" -> "f122",
      "icon-reply" -> "f112",
      "icon-resize-full" -> "f065",
      "icon-resize-horizontal" -> "f07e",
      "icon-resize-small" -> "f066",
      "icon-resize-vertical" -> "f07d",
      "icon-retweet" -> "f079",
      "icon-road" -> "f018",
      "icon-rocket" -> "f135",
      "icon-rss" -> "f09e",
      "icon-rss-sign" -> "f143",
      "icon-save" -> "f0c7",
      "icon-screenshot" -> "f05b",
      "icon-search" -> "f002",
      "icon-share-alt" -> "f064",
      "icon-share" -> "f045",
      "icon-share-sign" -> "f14d",
      "icon-shield" -> "f132",
      "icon-shopping-cart" -> "f07a",
      "icon-signal" -> "f012",
      "icon-sign-blank" -> "f0c8",
      "icon-signin" -> "f090",
      "icon-signout" -> "f08b",
      "icon-sitemap" -> "f0e8",
      "icon-skype" -> "f17e",
      "icon-smile" -> "f118",
      "icon-sort-by-alphabet-alt" -> "f15e",
      "icon-sort-by-alphabet" -> "f15d",
      "icon-sort-by-attributes-alt" -> "f161",
      "icon-sort-by-attributes" -> "f160",
      "icon-sort-by-order-alt" -> "f163",
      "icon-sort-by-order" -> "f162",
      "icon-sort-down" -> "f0dd",
      "icon-sort" -> "f0dc",
      "icon-sort-up" -> "f0de",
      "icon-spinner" -> "f110",
      "icon-stackexchange" -> "f16c",
      "icon-star-empty" -> "f006",
      "icon-star" -> "f005",
      "icon-star-half-empty" -> "f123",
      "icon-star-half" -> "f089",
      "icon-step-backward" -> "f048",
      "icon-step-forward" -> "f051",
      "icon-stethoscope" -> "f0f1",
      "icon-stop" -> "f04d",
      "icon-strikethrough" -> "f0cc",
      "icon-subscript" -> "f12c",
      "icon-suitcase" -> "f0f2",
      "icon-sun" -> "f185",
      "icon-superscript" -> "f12b",
      "icon-table" -> "f0ce",
      "icon-tablet" -> "f10a",
      "icon-tag" -> "f02b",
      "icon-tags" -> "f02c",
      "icon-tasks" -> "f0ae",
      "icon-terminal" -> "f120",
      "icon-text-height" -> "f034",
      "icon-text-width" -> "f035",
      "icon-th" -> "f00a",
      "icon-th-large" -> "f009",
      "icon-th-list" -> "f00b",
      "icon-thumbs-down-alt" -> "f088",
      "icon-thumbs-down" -> "f165",
      "icon-thumbs-up-alt" -> "f087",
      "icon-thumbs-up" -> "f164",
      "icon-ticket" -> "f145",
      "icon-time" -> "f017",
      "icon-tint" -> "f043",
      "icon-trash" -> "f014",
      "icon-trello" -> "f181",
      "icon-trophy" -> "f091",
      "icon-truck" -> "f0d1",
      "icon-tumblr" -> "f173",
      "icon-tumblr-sign" -> "f174",
      "icon-twitter" -> "f099",
      "icon-twitter-sign" -> "f081",
      "icon-umbrella" -> "f0e9",
      "icon-underline" -> "f0cd",
      "icon-undo" -> "f0e2",
      "icon-unlink" -> "f127",
      "icon-unlock-alt" -> "f13e",
      "icon-unlock" -> "f09c",
      "icon-upload-alt" -> "f093",
      "icon-upload" -> "f01b",
      "icon-usd" -> "f155",
      "icon-user" -> "f007",
      "icon-user-md" -> "f0f0",
      "icon-vk" -> "f189",
      "icon-volume-down" -> "f027",
      "icon-volume-off" -> "f026",
      "icon-volume-up" -> "f028",
      "icon-warning-sign" -> "f071",
      "icon-weibo" -> "f18a",
      "icon-windows" -> "f17a",
      "icon-wrench" -> "f0ad",
      "icon-xing" -> "f168",
      "icon-xing-sign" -> "f169",
      "icon-youtube" -> "f167",
      "icon-youtube-play" -> "f16a",
      "icon-youtube-sign" -> "f166",
      "icon-zoom-in" -> "f00e",
      "icon-zoom-out" -> "f010"
    ).mapValues(Integer.parseInt(_, 16).toChar)

  case class IconExt(icon: Icon) {


    lazy val name =
      icon.toString
        .replaceAll("^fa-", "")
        .replace("-", " ")
        .foldLeft("")((acc, c) => if (acc.endsWith(" ")) acc + c.toUpper else acc + c)
        .capitalize
    lazy val html = unicode(icon + "") + ""
  }

  implicit def toIconExt(icon: Icon) = IconExt(icon)
}
