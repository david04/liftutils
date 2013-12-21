package com.github.david04.liftutils.fontawesome

import scala.xml.Unparsed


object Icon extends Enumeration {
  type Icon = Value

  val adjust = Value("fa-adjust")
  val adn = Value("fa-adn")
  val align_center = Value("fa-align-center")
  val align_justify = Value("fa-align-justify")
  val align_left = Value("fa-align-left")
  val align_right = Value("fa-align-right")
  val ambulance = Value("fa-ambulance")
  val anchor = Value("fa-anchor")
  val android = Value("fa-android")
  val angle_double_down = Value("fa-angle-double-down")
  val angle_double_left = Value("fa-angle-double-left")
  val angle_double_right = Value("fa-angle-double-right")
  val angle_double_up = Value("fa-angle-double-up")
  val angle_down = Value("fa-angle-down")
  val angle_left = Value("fa-angle-left")
  val angle_right = Value("fa-angle-right")
  val angle_up = Value("fa-angle-up")
  val apple = Value("fa-apple")
  val archive = Value("fa-archive")
  val arrow_circle_down = Value("fa-arrow-circle-down")
  val arrow_circle_left = Value("fa-arrow-circle-left")
  val arrow_circle_o_down = Value("fa-arrow-circle-o-down")
  val arrow_circle_o_left = Value("fa-arrow-circle-o-left")
  val arrow_circle_o_right = Value("fa-arrow-circle-o-right")
  val arrow_circle_o_up = Value("fa-arrow-circle-o-up")
  val arrow_circle_right = Value("fa-arrow-circle-right")
  val arrow_circle_up = Value("fa-arrow-circle-up")
  val arrow_down = Value("fa-arrow-down")
  val arrow_left = Value("fa-arrow-left")
  val arrow_right = Value("fa-arrow-right")
  val arrows_alt = Value("fa-arrows-alt")
  val arrows_h = Value("fa-arrows-h")
  val arrows = Value("fa-arrows")
  val arrows_v = Value("fa-arrows-v")
  val arrow_up = Value("fa-arrow-up")
  val asterisk = Value("fa-asterisk")
  val backward = Value("fa-backward")
  val ban = Value("fa-ban")
  val bar_chart_o = Value("fa-bar-chart-o")
  val barcode = Value("fa-barcode")
  val bars = Value("fa-bars")
  val beer = Value("fa-beer")
  val bell_o = Value("fa-bell-o")
  val bell = Value("fa-bell")
  val bitbucket_square = Value("fa-bitbucket-square")
  val bitbucket = Value("fa-bitbucket")
  val bold = Value("fa-bold")
  val bolt = Value("fa-bolt")
  val bookmark_o = Value("fa-bookmark-o")
  val bookmark = Value("fa-bookmark")
  val book = Value("fa-book")
  val briefcase = Value("fa-briefcase")
  val btc = Value("fa-btc")
  val bug = Value("fa-bug")
  val building_o = Value("fa-building-o")
  val bullhorn = Value("fa-bullhorn")
  val bullseye = Value("fa-bullseye")
  val calendar_o = Value("fa-calendar-o")
  val calendar = Value("fa-calendar")
  val camera_retro = Value("fa-camera-retro")
  val camera = Value("fa-camera")
  val caret_down = Value("fa-caret-down")
  val caret_left = Value("fa-caret-left")
  val caret_right = Value("fa-caret-right")
  val caret_square_o_down = Value("fa-caret-square-o-down")
  val caret_square_o_left = Value("fa-caret-square-o-left")
  val caret_square_o_right = Value("fa-caret-square-o-right")
  val caret_square_o_up = Value("fa-caret-square-o-up")
  val caret_up = Value("fa-caret-up")
  val certificate = Value("fa-certificate")
  val chain_broken = Value("fa-chain-broken")
  val check_circle_o = Value("fa-check-circle-o")
  val check_circle = Value("fa-check-circle")
  val check_square_o = Value("fa-check-square-o")
  val check_square = Value("fa-check-square")
  val check = Value("fa-check")
  val chevron_circle_down = Value("fa-chevron-circle-down")
  val chevron_circle_left = Value("fa-chevron-circle-left")
  val chevron_circle_right = Value("fa-chevron-circle-right")
  val chevron_circle_up = Value("fa-chevron-circle-up")
  val chevron_down = Value("fa-chevron-down")
  val chevron_left = Value("fa-chevron-left")
  val chevron_right = Value("fa-chevron-right")
  val chevron_up = Value("fa-chevron-up")
  val circle_o = Value("fa-circle-o")
  val circle = Value("fa-circle")
  val clipboard = Value("fa-clipboard")
  val clock_o = Value("fa-clock-o")
  val cloud_download = Value("fa-cloud-download")
  val cloud_upload = Value("fa-cloud-upload")
  val cloud = Value("fa-cloud")
  val code_fork = Value("fa-code-fork")
  val code = Value("fa-code")
  val coffee = Value("fa-coffee")
  val cogs = Value("fa-cogs")
  val cog = Value("fa-cog")
  val columns = Value("fa-columns")
  val comment_o = Value("fa-comment-o")
  val comments_o = Value("fa-comments-o")
  val comments = Value("fa-comments")
  val comment = Value("fa-comment")
  val compass = Value("fa-compass")
  val compress = Value("fa-compress")
  val credit_card = Value("fa-credit-card")
  val crop = Value("fa-crop")
  val crosshairs = Value("fa-crosshairs")
  val css3 = Value("fa-css3")
  val cutlery = Value("fa-cutlery")
  val desktop = Value("fa-desktop")
  val dot_circle_o = Value("fa-dot-circle-o")
  val download = Value("fa-download")
  val dribbble = Value("fa-dribbble")
  val dropbox = Value("fa-dropbox")
  val eject = Value("fa-eject")
  val ellipsis_h = Value("fa-ellipsis-h")
  val ellipsis_v = Value("fa-ellipsis-v")
  val envelope_o = Value("fa-envelope-o")
  val envelope = Value("fa-envelope")
  val eraser = Value("fa-eraser")
  val eur = Value("fa-eur")
  val exchange = Value("fa-exchange")
  val exclamation_circle = Value("fa-exclamation-circle")
  val exclamation_triangle = Value("fa-exclamation-triangle")
  val exclamation = Value("fa-exclamation")
  val expand = Value("fa-expand")
  val external_link_square = Value("fa-external-link-square")
  val external_link = Value("fa-external-link")
  val eye_slash = Value("fa-eye-slash")
  val eye = Value("fa-eye")
  val facebook_square = Value("fa-facebook-square")
  val facebook = Value("fa-facebook")
  val fast_backward = Value("fa-fast-backward")
  val fast_forward = Value("fa-fast-forward")
  val female = Value("fa-female")
  val fighter_jet = Value("fa-fighter-jet")
  val file_o = Value("fa-file-o")
  val files_o = Value("fa-files-o")
  val file_text_o = Value("fa-file-text-o")
  val file_text = Value("fa-file-text")
  val file = Value("fa-file")
  val film = Value("fa-film")
  val filter = Value("fa-filter")
  val fire_extinguisher = Value("fa-fire-extinguisher")
  val fire = Value("fa-fire")
  val flag_checkered = Value("fa-flag-checkered")
  val flag_o = Value("fa-flag-o")
  val flag = Value("fa-flag")
  val flask = Value("fa-flask")
  val flickr = Value("fa-flickr")
  val floppy_o = Value("fa-floppy-o")
  val folder_open_o = Value("fa-folder-open-o")
  val folder_open = Value("fa-folder-open")
  val folder_o = Value("fa-folder-o")
  val folder = Value("fa-folder")
  val font = Value("fa-font")
  val forward = Value("fa-forward")
  val foursquare = Value("fa-foursquare")
  val frown_o = Value("fa-frown-o")
  val gamepad = Value("fa-gamepad")
  val gavel = Value("fa-gavel")
  val gbp = Value("fa-gbp")
  val gift = Value("fa-gift")
  val github_alt = Value("fa-github-alt")
  val github_square = Value("fa-github-square")
  val github = Value("fa-github")
  val gittip = Value("fa-gittip")
  val glass = Value("fa-glass")
  val globe = Value("fa-globe")
  val google_plus_square = Value("fa-google-plus-square")
  val google_plus = Value("fa-google-plus")
  val hand_o_down = Value("fa-hand-o-down")
  val hand_o_left = Value("fa-hand-o-left")
  val hand_o_right = Value("fa-hand-o-right")
  val hand_o_up = Value("fa-hand-o-up")
  val hdd_o = Value("fa-hdd-o")
  val headphones = Value("fa-headphones")
  val heart_o = Value("fa-heart-o")
  val heart = Value("fa-heart")
  val home = Value("fa-home")
  val hospital_o = Value("fa-hospital-o")
  val h_square = Value("fa-h-square")
  val html5 = Value("fa-html5")
  val inbox = Value("fa-inbox")
  val indent = Value("fa-indent")
  val info_circle = Value("fa-info-circle")
  val info = Value("fa-info")
  val inr = Value("fa-inr")
  val instagram = Value("fa-instagram")
  val italic = Value("fa-italic")
  val jpy = Value("fa-jpy")
  val keyboard_o = Value("fa-keyboard-o")
  val key = Value("fa-key")
  val krw = Value("fa-krw")
  val laptop = Value("fa-laptop")
  val leaf = Value("fa-leaf")
  val lemon_o = Value("fa-lemon-o")
  val level_down = Value("fa-level-down")
  val level_up = Value("fa-level-up")
  val lightbulb_o = Value("fa-lightbulb-o")
  val linkedin_square = Value("fa-linkedin-square")
  val linkedin = Value("fa-linkedin")
  val link = Value("fa-link")
  val linux = Value("fa-linux")
  val list_alt = Value("fa-list-alt")
  val list_ol = Value("fa-list-ol")
  val list_ul = Value("fa-list-ul")
  val list = Value("fa-list")
  val location_arrow = Value("fa-location-arrow")
  val lock = Value("fa-lock")
  val long_arrow_down = Value("fa-long-arrow-down")
  val long_arrow_left = Value("fa-long-arrow-left")
  val long_arrow_right = Value("fa-long-arrow-right")
  val long_arrow_up = Value("fa-long-arrow-up")
  val magic = Value("fa-magic")
  val magnet = Value("fa-magnet")
  val mail_reply_all = Value("fa-mail-reply-all")
  val male = Value("fa-male")
  val map_marker = Value("fa-map-marker")
  val maxcdn = Value("fa-maxcdn")
  val medkit = Value("fa-medkit")
  val meh_o = Value("fa-meh-o")
  val microphone_slash = Value("fa-microphone-slash")
  val microphone = Value("fa-microphone")
  val minus_circle = Value("fa-minus-circle")
  val minus_square_o = Value("fa-minus-square-o")
  val minus_square = Value("fa-minus-square")
  val minus = Value("fa-minus")
  val mobile = Value("fa-mobile")
  val money = Value("fa-money")
  val moon_o = Value("fa-moon-o")
  val music = Value("fa-music")
  val outdent = Value("fa-outdent")
  val pagelines = Value("fa-pagelines")
  val paperclip = Value("fa-paperclip")
  val pause = Value("fa-pause")
  val pencil_square_o = Value("fa-pencil-square-o")
  val pencil_square = Value("fa-pencil-square")
  val pencil = Value("fa-pencil")
  val phone_square = Value("fa-phone-square")
  val phone = Value("fa-phone")
  val picture_o = Value("fa-picture-o")
  val pinterest_square = Value("fa-pinterest-square")
  val pinterest = Value("fa-pinterest")
  val plane = Value("fa-plane")
  val play_circle_o = Value("fa-play-circle-o")
  val play_circle = Value("fa-play-circle")
  val play = Value("fa-play")
  val plus_circle = Value("fa-plus-circle")
  val plus_square_o = Value("fa-plus-square-o")
  val plus_square = Value("fa-plus-square")
  val plus = Value("fa-plus")
  val power_off = Value("fa-power-off")
  val print = Value("fa-print")
  val puzzle_piece = Value("fa-puzzle-piece")
  val qrcode = Value("fa-qrcode")
  val question_circle = Value("fa-question-circle")
  val question = Value("fa-question")
  val quote_left = Value("fa-quote-left")
  val quote_right = Value("fa-quote-right")
  val random = Value("fa-random")
  val refresh = Value("fa-refresh")
  val renren = Value("fa-renren")
  val repeat = Value("fa-repeat")
  val reply_all = Value("fa-reply-all")
  val reply = Value("fa-reply")
  val retweet = Value("fa-retweet")
  val road = Value("fa-road")
  val rocket = Value("fa-rocket")
  val rss_square = Value("fa-rss-square")
  val rss = Value("fa-rss")
  val rub = Value("fa-rub")
  val scissors = Value("fa-scissors")
  val search_minus = Value("fa-search-minus")
  val search_plus = Value("fa-search-plus")
  val search = Value("fa-search")
  val share_square_o = Value("fa-share-square-o")
  val share_square = Value("fa-share-square")
  val share = Value("fa-share")
  val shield = Value("fa-shield")
  val shopping_cart = Value("fa-shopping-cart")
  val signal = Value("fa-signal")
  val sign_in = Value("fa-sign-in")
  val sign_out = Value("fa-sign-out")
  val sitemap = Value("fa-sitemap")
  val skype = Value("fa-skype")
  val smile_o = Value("fa-smile-o")
  val sort_alpha_asc = Value("fa-sort-alpha-asc")
  val sort_alpha_desc = Value("fa-sort-alpha-desc")
  val sort_amount_asc = Value("fa-sort-amount-asc")
  val sort_amount_desc = Value("fa-sort-amount-desc")
  val sort_asc = Value("fa-sort-asc")
  val sort_desc = Value("fa-sort-desc")
  val sort_numeric_asc = Value("fa-sort-numeric-asc")
  val sort_numeric_desc = Value("fa-sort-numeric-desc")
  val sort = Value("fa-sort")
  val spinner = Value("fa-spinner")
  val square_o = Value("fa-square-o")
  val square = Value("fa-square")
  val stack_exchange = Value("fa-stack-exchange")
  val stack_overflow = Value("fa-stack-overflow")
  val star_half_o = Value("fa-star-half-o")
  val star_half = Value("fa-star-half")
  val star_o = Value("fa-star-o")
  val star = Value("fa-star")
  val step_backward = Value("fa-step-backward")
  val step_forward = Value("fa-step-forward")
  val stethoscope = Value("fa-stethoscope")
  val stop = Value("fa-stop")
  val strikethrough = Value("fa-strikethrough")
  val subscript = Value("fa-subscript")
  val suitcase = Value("fa-suitcase")
  val sun_o = Value("fa-sun-o")
  val superscript = Value("fa-superscript")
  val tablet = Value("fa-tablet")
  val table = Value("fa-table")
  val tachometer = Value("fa-tachometer")
  val tags = Value("fa-tags")
  val tag = Value("fa-tag")
  val tasks = Value("fa-tasks")
  val terminal = Value("fa-terminal")
  val text_height = Value("fa-text-height")
  val text_width = Value("fa-text-width")
  val th_large = Value("fa-th-large")
  val th_list = Value("fa-th-list")
  val thumbs_down = Value("fa-thumbs-down")
  val thumbs_o_down = Value("fa-thumbs-o-down")
  val thumbs_o_up = Value("fa-thumbs-o-up")
  val thumbs_up = Value("fa-thumbs-up")
  val thumb_tack = Value("fa-thumb-tack")
  val th = Value("fa-th")
  val ticket = Value("fa-ticket")
  val times_circle_o = Value("fa-times-circle-o")
  val times_circle = Value("fa-times-circle")
  val times = Value("fa-times")
  val tint = Value("fa-tint")
  val trash_o = Value("fa-trash-o")
  val trello = Value("fa-trello")
  val trophy = Value("fa-trophy")
  val truck = Value("fa-truck")
  val try_ = Value("fa-try")
  val tumblr_square = Value("fa-tumblr-square")
  val tumblr = Value("fa-tumblr")
  val twitter_square = Value("fa-twitter-square")
  val twitter = Value("fa-twitter")
  val umbrella = Value("fa-umbrella")
  val underline = Value("fa-underline")
  val undo = Value("fa-undo")
  val unlock_alt = Value("fa-unlock-alt")
  val unlock = Value("fa-unlock")
  val upload = Value("fa-upload")
  val usd = Value("fa-usd")
  val user_md = Value("fa-user-md")
  val users = Value("fa-users")
  val user = Value("fa-user")
  val video_camera = Value("fa-video-camera")
  val vimeo_square = Value("fa-vimeo-square")
  val vk = Value("fa-vk")
  val volume_down = Value("fa-volume-down")
  val volume_off = Value("fa-volume-off")
  val volume_up = Value("fa-volume-up")
  val weibo = Value("fa-weibo")
  val wheelchair = Value("fa-wheelchair")
  val windows = Value("fa-windows")
  val wrench = Value("fa-wrench")
  val xing_square = Value("fa-xing-square")
  val xing = Value("fa-xing")
  val youtube_play = Value("fa-youtube-play")
  val youtube_square = Value("fa-youtube-square")
  val youtube = Value("fa-youtube")

  val unicode =
    Map(
      "fa-adjust" -> "f042",
      "fa-adn" -> "f170",
      "fa-align-center" -> "f037",
      "fa-align-justify" -> "f039",
      "fa-align-left" -> "f036",
      "fa-align-right" -> "f038",
      "fa-ambulance" -> "f0f9",
      "fa-anchor" -> "f13d",
      "fa-android" -> "f17b",
      "fa-angle-double-down" -> "f103",
      "fa-angle-double-left" -> "f100",
      "fa-angle-double-right" -> "f101",
      "fa-angle-double-up" -> "f102",
      "fa-angle-down" -> "f107",
      "fa-angle-left" -> "f104",
      "fa-angle-right" -> "f105",
      "fa-angle-up" -> "f106",
      "fa-apple" -> "f179",
      "fa-archive" -> "f187",
      "fa-arrow-circle-down" -> "f0ab",
      "fa-arrow-circle-left" -> "f0a8",
      "fa-arrow-circle-o-down" -> "f01a",
      "fa-arrow-circle-o-left" -> "f190",
      "fa-arrow-circle-o-right" -> "f18e",
      "fa-arrow-circle-o-up" -> "f01b",
      "fa-arrow-circle-right" -> "f0a9",
      "fa-arrow-circle-up" -> "f0aa",
      "fa-arrow-down" -> "f063",
      "fa-arrow-left" -> "f060",
      "fa-arrow-right" -> "f061",
      "fa-arrows-alt" -> "f0b2",
      "fa-arrows" -> "f047",
      "fa-arrows-h" -> "f07e",
      "fa-arrows-v" -> "f07d",
      "fa-arrow-up" -> "f062",
      "fa-asterisk" -> "f069",
      "fa-backward" -> "f04a",
      "fa-ban" -> "f05e",
      "fa-bar-chart-o" -> "f080",
      "fa-barcode" -> "f02a",
      "fa-bars" -> "f0c9",
      "fa-beer" -> "f0fc",
      "fa-bell" -> "f0f3",
      "fa-bell-o" -> "f0a2",
      "fa-bitbucket" -> "f171",
      "fa-bitbucket-square" -> "f172",
      "fa-bold" -> "f032",
      "fa-bolt" -> "f0e7",
      "fa-book" -> "f02d",
      "fa-bookmark" -> "f02e",
      "fa-bookmark-o" -> "f097",
      "fa-briefcase" -> "f0b1",
      "fa-btc" -> "f15a",
      "fa-bug" -> "f188",
      "fa-building-o" -> "f0f7",
      "fa-bullhorn" -> "f0a1",
      "fa-bullseye" -> "f140",
      "fa-calendar" -> "f073",
      "fa-calendar-o" -> "f133",
      "fa-camera" -> "f030",
      "fa-camera-retro" -> "f083",
      "fa-caret-down" -> "f0d7",
      "fa-caret-left" -> "f0d9",
      "fa-caret-right" -> "f0da",
      "fa-caret-square-o-down" -> "f150",
      "fa-caret-square-o-left" -> "f191",
      "fa-caret-square-o-right" -> "f152",
      "fa-caret-square-o-up" -> "f151",
      "fa-caret-up" -> "f0d8",
      "fa-certificate" -> "f0a3",
      "fa-chain-broken" -> "f127",
      "fa-check-circle" -> "f058",
      "fa-check-circle-o" -> "f05d",
      "fa-check" -> "f00c",
      "fa-check-square" -> "f14a",
      "fa-check-square-o" -> "f046",
      "fa-chevron-circle-down" -> "f13a",
      "fa-chevron-circle-left" -> "f137",
      "fa-chevron-circle-right" -> "f138",
      "fa-chevron-circle-up" -> "f139",
      "fa-chevron-down" -> "f078",
      "fa-chevron-left" -> "f053",
      "fa-chevron-right" -> "f054",
      "fa-chevron-up" -> "f077",
      "fa-circle" -> "f111",
      "fa-circle-o" -> "f10c",
      "fa-clipboard" -> "f0ea",
      "fa-clock-o" -> "f017",
      "fa-cloud-download" -> "f0ed",
      "fa-cloud" -> "f0c2",
      "fa-cloud-upload" -> "f0ee",
      "fa-code" -> "f121",
      "fa-code-fork" -> "f126",
      "fa-coffee" -> "f0f4",
      "fa-cog" -> "f013",
      "fa-cogs" -> "f085",
      "fa-columns" -> "f0db",
      "fa-comment" -> "f075",
      "fa-comment-o" -> "f0e5",
      "fa-comments" -> "f086",
      "fa-comments-o" -> "f0e6",
      "fa-compass" -> "f14e",
      "fa-compress" -> "f066",
      "fa-credit-card" -> "f09d",
      "fa-crop" -> "f125",
      "fa-crosshairs" -> "f05b",
      "fa-css3" -> "f13c",
      "fa-cutlery" -> "f0f5",
      "fa-desktop" -> "f108",
      "fa-dot-circle-o" -> "f192",
      "fa-download" -> "f019",
      "fa-dribbble" -> "f17d",
      "fa-dropbox" -> "f16b",
      "fa-eject" -> "f052",
      "fa-ellipsis-h" -> "f141",
      "fa-ellipsis-v" -> "f142",
      "fa-envelope" -> "f0e0",
      "fa-envelope-o" -> "f003",
      "fa-eraser" -> "f12d",
      "fa-eur" -> "f153",
      "fa-exchange" -> "f0ec",
      "fa-exclamation-circle" -> "f06a",
      "fa-exclamation" -> "f12a",
      "fa-exclamation-triangle" -> "f071",
      "fa-expand" -> "f065",
      "fa-external-link" -> "f08e",
      "fa-external-link-square" -> "f14c",
      "fa-eye" -> "f06e",
      "fa-eye-slash" -> "f070",
      "fa-facebook" -> "f09a",
      "fa-facebook-square" -> "f082",
      "fa-fast-backward" -> "f049",
      "fa-fast-forward" -> "f050",
      "fa-female" -> "f182",
      "fa-fighter-jet" -> "f0fb",
      "fa-file" -> "f15b",
      "fa-file-o" -> "f016",
      "fa-files-o" -> "f0c5",
      "fa-file-text" -> "f15c",
      "fa-file-text-o" -> "f0f6",
      "fa-film" -> "f008",
      "fa-filter" -> "f0b0",
      "fa-fire-extinguisher" -> "f134",
      "fa-fire" -> "f06d",
      "fa-flag-checkered" -> "f11e",
      "fa-flag" -> "f024",
      "fa-flag-o" -> "f11d",
      "fa-flask" -> "f0c3",
      "fa-flickr" -> "f16e",
      "fa-floppy-o" -> "f0c7",
      "fa-folder" -> "f07b",
      "fa-folder-o" -> "f114",
      "fa-folder-open" -> "f07c",
      "fa-folder-open-o" -> "f115",
      "fa-font" -> "f031",
      "fa-forward" -> "f04e",
      "fa-foursquare" -> "f180",
      "fa-frown-o" -> "f119",
      "fa-gamepad" -> "f11b",
      "fa-gavel" -> "f0e3",
      "fa-gbp" -> "f154",
      "fa-gift" -> "f06b",
      "fa-github-alt" -> "f113",
      "fa-github" -> "f09b",
      "fa-github-square" -> "f092",
      "fa-gittip" -> "f184",
      "fa-glass" -> "f000",
      "fa-globe" -> "f0ac",
      "fa-google-plus" -> "f0d5",
      "fa-google-plus-square" -> "f0d4",
      "fa-hand-o-down" -> "f0a7",
      "fa-hand-o-left" -> "f0a5",
      "fa-hand-o-right" -> "f0a4",
      "fa-hand-o-up" -> "f0a6",
      "fa-hdd-o" -> "f0a0",
      "fa-headphones" -> "f025",
      "fa-heart" -> "f004",
      "fa-heart-o" -> "f08a",
      "fa-home" -> "f015",
      "fa-hospital-o" -> "f0f8",
      "fa-h-square" -> "f0fd",
      "fa-html5" -> "f13b",
      "fa-inbox" -> "f01c",
      "fa-indent" -> "f03c",
      "fa-info-circle" -> "f05a",
      "fa-info" -> "f129",
      "fa-inr" -> "f156",
      "fa-instagram" -> "f16d",
      "fa-italic" -> "f033",
      "fa-jpy" -> "f157",
      "fa-keyboard-o" -> "f11c",
      "fa-key" -> "f084",
      "fa-krw" -> "f159",
      "fa-laptop" -> "f109",
      "fa-leaf" -> "f06c",
      "fa-lemon-o" -> "f094",
      "fa-level-down" -> "f149",
      "fa-level-up" -> "f148",
      "fa-lightbulb-o" -> "f0eb",
      "fa-linkedin" -> "f0e1",
      "fa-linkedin-square" -> "f08c",
      "fa-link" -> "f0c1",
      "fa-linux" -> "f17c",
      "fa-list-alt" -> "f022",
      "fa-list" -> "f03a",
      "fa-list-ol" -> "f0cb",
      "fa-list-ul" -> "f0ca",
      "fa-location-arrow" -> "f124",
      "fa-lock" -> "f023",
      "fa-long-arrow-down" -> "f175",
      "fa-long-arrow-left" -> "f177",
      "fa-long-arrow-right" -> "f178",
      "fa-long-arrow-up" -> "f176",
      "fa-magic" -> "f0d0",
      "fa-magnet" -> "f076",
      "fa-mail-reply-all" -> "f122",
      "fa-male" -> "f183",
      "fa-map-marker" -> "f041",
      "fa-maxcdn" -> "f136",
      "fa-medkit" -> "f0fa",
      "fa-meh-o" -> "f11a",
      "fa-microphone" -> "f130",
      "fa-microphone-slash" -> "f131",
      "fa-minus-circle" -> "f056",
      "fa-minus" -> "f068",
      "fa-minus-square" -> "f146",
      "fa-minus-square-o" -> "f147",
      "fa-mobile" -> "f10b",
      "fa-money" -> "f0d6",
      "fa-moon-o" -> "f186",
      "fa-music" -> "f001",
      "fa-outdent" -> "f03b",
      "fa-pagelines" -> "f18c",
      "fa-paperclip" -> "f0c6",
      "fa-pause" -> "f04c",
      "fa-pencil" -> "f040",
      "fa-pencil-square" -> "f14b",
      "fa-pencil-square-o" -> "f044",
      "fa-phone" -> "f095",
      "fa-phone-square" -> "f098",
      "fa-picture-o" -> "f03e",
      "fa-pinterest" -> "f0d2",
      "fa-pinterest-square" -> "f0d3",
      "fa-plane" -> "f072",
      "fa-play-circle" -> "f144",
      "fa-play-circle-o" -> "f01d",
      "fa-play" -> "f04b",
      "fa-plus-circle" -> "f055",
      "fa-plus" -> "f067",
      "fa-plus-square" -> "f0fe",
      "fa-plus-square-o" -> "f196",
      "fa-power-off" -> "f011",
      "fa-print" -> "f02f",
      "fa-puzzle-piece" -> "f12e",
      "fa-qrcode" -> "f029",
      "fa-question-circle" -> "f059",
      "fa-question" -> "f128",
      "fa-quote-left" -> "f10d",
      "fa-quote-right" -> "f10e",
      "fa-random" -> "f074",
      "fa-refresh" -> "f021",
      "fa-renren" -> "f18b",
      "fa-repeat" -> "f01e",
      "fa-reply-all" -> "f122",
      "fa-reply" -> "f112",
      "fa-retweet" -> "f079",
      "fa-road" -> "f018",
      "fa-rocket" -> "f135",
      "fa-rss" -> "f09e",
      "fa-rss-square" -> "f143",
      "fa-rub" -> "f158",
      "fa-scissors" -> "f0c4",
      "fa-search" -> "f002",
      "fa-search-minus" -> "f010",
      "fa-search-plus" -> "f00e",
      "fa-share" -> "f064",
      "fa-share-square" -> "f14d",
      "fa-share-square-o" -> "f045",
      "fa-shield" -> "f132",
      "fa-shopping-cart" -> "f07a",
      "fa-signal" -> "f012",
      "fa-sign-in" -> "f090",
      "fa-sign-out" -> "f08b",
      "fa-sitemap" -> "f0e8",
      "fa-skype" -> "f17e",
      "fa-smile-o" -> "f118",
      "fa-sort-alpha-asc" -> "f15d",
      "fa-sort-alpha-desc" -> "f15e",
      "fa-sort-amount-asc" -> "f160",
      "fa-sort-amount-desc" -> "f161",
      "fa-sort-asc" -> "f0dd",
      "fa-sort-desc" -> "f0de",
      "fa-sort" -> "f0dc",
      "fa-sort-numeric-asc" -> "f162",
      "fa-sort-numeric-desc" -> "f163",
      "fa-spinner" -> "f110",
      "fa-square" -> "f0c8",
      "fa-square-o" -> "f096",
      "fa-stack-exchange" -> "f18d",
      "fa-stack-overflow" -> "f16c",
      "fa-star" -> "f005",
      "fa-star-half" -> "f089",
      "fa-star-half-o" -> "f123",
      "fa-star-o" -> "f006",
      "fa-step-backward" -> "f048",
      "fa-step-forward" -> "f051",
      "fa-stethoscope" -> "f0f1",
      "fa-stop" -> "f04d",
      "fa-strikethrough" -> "f0cc",
      "fa-subscript" -> "f12c",
      "fa-suitcase" -> "f0f2",
      "fa-sun-o" -> "f185",
      "fa-superscript" -> "f12b",
      "fa-table" -> "f0ce",
      "fa-tablet" -> "f10a",
      "fa-tachometer" -> "f0e4",
      "fa-tag" -> "f02b",
      "fa-tags" -> "f02c",
      "fa-tasks" -> "f0ae",
      "fa-terminal" -> "f120",
      "fa-text-height" -> "f034",
      "fa-text-width" -> "f035",
      "fa-th" -> "f00a",
      "fa-th-large" -> "f009",
      "fa-th-list" -> "f00b",
      "fa-thumbs-down" -> "f165",
      "fa-thumbs-o-down" -> "f088",
      "fa-thumbs-o-up" -> "f087",
      "fa-thumbs-up" -> "f164",
      "fa-thumb-tack" -> "f08d",
      "fa-ticket" -> "f145",
      "fa-times-circle" -> "f057",
      "fa-times-circle-o" -> "f05c",
      "fa-times" -> "f00d",
      "fa-tint" -> "f043",
      "fa-trash-o" -> "f014",
      "fa-trello" -> "f181",
      "fa-trophy" -> "f091",
      "fa-truck" -> "f0d1",
      "fa-try" -> "f195",
      "fa-tumblr" -> "f173",
      "fa-tumblr-square" -> "f174",
      "fa-twitter" -> "f099",
      "fa-twitter-square" -> "f081",
      "fa-umbrella" -> "f0e9",
      "fa-underline" -> "f0cd",
      "fa-undo" -> "f0e2",
      "fa-unlock-alt" -> "f13e",
      "fa-unlock" -> "f09c",
      "fa-upload" -> "f093",
      "fa-usd" -> "f155",
      "fa-user" -> "f007",
      "fa-user-md" -> "f0f0",
      "fa-users" -> "f0c0",
      "fa-video-camera" -> "f03d",
      "fa-vimeo-square" -> "f194",
      "fa-vk" -> "f189",
      "fa-volume-down" -> "f027",
      "fa-volume-off" -> "f026",
      "fa-volume-up" -> "f028",
      "fa-weibo" -> "f18a",
      "fa-wheelchair" -> "f193",
      "fa-windows" -> "f17a",
      "fa-wrench" -> "f0ad",
      "fa-xing" -> "f168",
      "fa-xing-square" -> "f169",
      "fa-youtube" -> "f167",
      "fa-youtube-play" -> "f16a",
      "fa-youtube-square" -> "f166"
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
