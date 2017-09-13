package de.gerdiproject.json.fao;

/**
 * This class represents the the metadata object, that is part of every JSON response from FaoSTAT.
 *
 * @author Robin Weiss
 *
 */
public class FaoResponseMetadata
{
    private double processing_time;
    private String output_type;

    public double getProcessing_time()
    {
        return processing_time;
    }

    public void setProcessing_time(double processing_time)
    {
        this.processing_time = processing_time;
    }

    public String getOutput_type()
    {
        return output_type;
    }

    public void setOutput_type(String output_type)
    {
        this.output_type = output_type;
    }
}
