These are examples of the files that are used by NFRIndexer to normalize NSDL DC records,
any by the NSDL DC vocab reports in DDS to show the vocabs that exist in the records and
how whey will be mapped for normalization.

The vocab_selections.xml file should be pointed to by NFRIndexer_conf.xml, 
and the base-directory pointed to in the DDS context-parameter 'nsdlDcVocabsConfig' for the DDS reports generator.

The groups files used for normalization are housed at (note iis-schemas-project was previously named nsdl-schemas-project):

iis-schemas-project/ncs/ddsws/1-1/groups - These are pointed to by the reports generator and used to determine new vocabs that come along
iis-schemas-project/ncs/ddsws/1-1/groupsNormal - These are pointed to by NFRIndexer_conf.xml and are used for normalizing the NSDL DC records in operational settings

