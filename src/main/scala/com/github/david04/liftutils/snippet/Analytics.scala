package com.github.david04.liftutils.snippet

import scala.xml.{NodeSeq, Unparsed}
import net.liftweb.util
import net.liftweb.common._
import net.liftweb.http.js.JsCmds.Run

/**
 * Created by david at 8:34 AM
 */
trait Analytics {

  def prefix: Option[String]

  private def prefixStr = prefix.map(_ + ".").getOrElse("")

  val analyticsToken = util.Props.get(s"app.${prefixStr}analitycs.google.token")
  val analyticsDomain = util.Props.get(s"app.${prefixStr}analitycs.google.domain")
  val analyticsConversionId = util.Props.get(s"app.${prefixStr}analitycs.google.conversion-id")
  val reinvigorateCode = util.Props.get(s"app.${prefixStr}analitycs.reinvigorate.code")
  val perfectAudience = util.Props.getBool(s"app.${prefixStr}analitycs.perfectaudience.enable")

  def render = (_: NodeSeq) =>

    (((analyticsToken, analyticsDomain) match {
      case (Full(token), Full(domain)) =>
        Full(<head_merge>{Unparsed(
          """
            |
            |    <script>
            |        (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
            |        (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
            |        m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
            |        })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
            |
            |        ga('create', '{token}', '{domain}');
            |        ga('send', 'pageview');
            |
            |    </script>
            |
          """.stripMargin
            .replaceAllLiterally("{token}", token)
            .replaceAllLiterally("{domain}", domain))}</head_merge>)
      case _ => Empty
    }) ::
      analyticsConversionId.map(conversionId => <tail>{
        Unparsed( """
                    |<script type="text/javascript">
                    |    /* <![CDATA[ */
                    |    var google_conversion_id = {conversion_id};
                    |    var google_custom_params = window.google_tag_params;
                    |    var google_remarketing_only = true;
                    |    /* ]]> */
                    |</script>
                    |<script type="text/javascript" src="//www.googleadservices.com/pagead/conversion.js">
                    |</script>
                    |<noscript>
                    |    <div style="display:inline;">
                    |        <img height="1" width="1" style="border-style:none;" alt="" src="//googleads.g.doubleclick.net/pagead/viewthroughconversion/{conversion_id}/?value=0&amp;guid=ON&amp;script=0"/>
                    |    </div>
                    |</noscript>
                  """.stripMargin
          .replaceAllLiterally("{conversion_id}", conversionId))
      }</tail>) ::
      reinvigorateCode.map(code => <tail>{
        Unparsed( """
                    |<script type="text/javascript" src="http://include.reinvigorate.net/re_.js"></script>
                    |<script type="text/javascript">
                    |    try {
                    |    reinvigorate.track("{code}");
                    |    } catch(err) {}
                    |</script>
                  """.stripMargin
          .replaceAllLiterally("{code}", code))
      }</tail>) ::
      (if (perfectAudience == Full(true))
        Full(<tail>{
          Unparsed(
            """
              |<script type="text/javascript">
              |  (function() {
              |    window._pa = window._pa || {};
              |    // _pa.orderId = "myCustomer@email.com"; // OPTIONAL: attach user email or order ID to conversions
              |    // _pa.revenue = "19.99"; // OPTIONAL: attach dynamic purchase values to conversions
              |    var pa = document.createElement('script'); pa.type = 'text/javascript'; pa.async = true;
              |    pa.src = ('https:' == document.location.protocol ? 'https:' : 'http:') + "//tag.perfectaudience.com/serve/525eb679355988150f000066.js";
              |    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(pa, s);
              |  })();
              |</script>
            """.stripMargin
          )
        }</tail>)
      else Empty) ::
      Full(NodeSeq.Empty) ::
      Nil)
      .flatten.reduce(_ ++ _)

  object Adwords {
    def conversion(conversionLabel: String) = Run({
      "var image = new Image(1,1);" +
        s"image.src = 'http://www.googleadservices.com/pagead/conversion/${analyticsConversionId.get}/?label=$conversionLabel';"
    })
  }

}

object Analytics extends Analytics {def prefix = None}