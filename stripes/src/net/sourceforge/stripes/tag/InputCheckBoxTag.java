package net.sourceforge.stripes.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;

/**
 * <p>Implements an HTML tag that generates form fields of type {@literal <input type="checkbox"/>}.
 * Since a single checkbox widget on a HTML page can have only a single value, the value tag
 * attribute must always resolve to a String (though this is somewhat lax since the EL will coerce
 * almost anything to a String).</p>
 *
 * <p>Checkboxes perform automatic (re-)population of state.  They prefer, in order, values in the
 * HttpServletRequest, values in the ActionBean and lastly values set using checked="" on the page.
 * The "checked" attribute is a complex attribute and may be a Collection, an Array or a scalar
 * Java Object.  In the first two cases a check is performed to see if the value in the value="foo"
 * attribute is one of the elements in the checked collection or array.  In the last case, the
 * value is matched directly against the String form of the checked attribute.  If in any case a
 * checkbox's value matches then a checked="checked" attribute will be added to the HTML written.</p>
 *
 * <p>The tag may include a body and if present the body is converted to a String and overrides the
 * <b>checked</b> tag attribute.</p>
 *
 * @author Tim Fennell
 */
public class InputCheckBoxTag extends InputTagSupport implements BodyTag {
    private Object checked;

    /** Basic constructor that sets the input tag's type attribute to "checkbox". */
    public InputCheckBoxTag() {
        super();
        getAttributes().put("type", "checkbox");
    }

    /**
     * Sets the default checked values for checkboxes with this name.
     *
     * @param checked may be either a Collection or Array of checked values, or a single Checked
     *        value.  Values do not have to be Strings, but will need to be convertible to String
     *        using the toString() method.
     */
    public void setChecked(Object checked) {
        this.checked = checked;
    }

    /** Returns the value originally set using setChecked(). */
    public Object getChecked() {
        return this.checked;
    }

    /** Sets the value that this checkbox will submit if it is checked. */
    public void setValue(String value) { set("value", value); }

    /** Returns the value that this checkbox will submit if it is checked. */
    public String getValue() { return get("value"); }


    /** Does nothing. */
    public int doStartInputTag() throws JspException {
        return EVAL_BODY_BUFFERED;
    }

    /** Does nothing. */
    public void doInitBody() throws JspException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /** Ensure that the body is evaluated only once. */
    public int doAfterBody() throws JspException {
        return SKIP_BODY;
    }

    /**
     * Does the main work of the tag, including determining the tags state (checked or not) and
     * writing out a singleton tag representing the checkbox.
     *
     * @return always returns EVAL_PAGE to continue page execution
     * @throws JspException if the checkbox is not contained inside a stripes InputFormTag, or has
     *         problems writing to the output.
     */
    public int doEndInputTag() throws JspException {
        // Find out if we have a value from the PopulationStrategy
        Object override = getOverrideValueOrValues();
        String body     = getBodyContentAsString();
        Object checked = null;

        // Figure out where to pull the default value from
        if (override != null) {
            checked = override;
        }
        else if (body != null) {
            checked = body;
        }
        else {
            checked = this.checked;
        }

        // If the value of this checkbox is contained in the value or override value, check it
        if (isItemSelected(getValue(), checked)) {
            getAttributes().put("checked", "checked");
        }

        writeSingletonTag(getPageContext().getOut(), "input");

        // Restore the tags state to before we mucked with it
        getAttributes().remove("checked");
        
        return EVAL_PAGE;
    }
}
