// Include this script tag in your HTML page before including the dds_search_jquery_implementation.js

// This configuration controls what facets and facet labels are displayed in the UI histogram views and search results.
// Add a facet category that is available from the given DDS API index

// Order determines where they appear in search results.
//

// These facets are available for NSDL DC collections:
var facetConfigurationSettings = {
    categories: {
        category: [

            {
                head: {
                    name: "EducationLevel", // The DDS facet category to render
                    label: "Education Level", // (optional) The alternate label to display in the UI for this category
                    orderBy: "labels" // (optional) Order by 'labels' - To render in the order provided here, otherwise order by results returned the DDS API
                },
                // By default, facet labels from the API are displayed.
                // (Optional) provide alternate labels if desired or omit a given facet altogether using omitFromViews:true
                labels: [
                    {
                        label: "Elementary School",
                        facet: "Elementary School"
                    },
                    {
                        label: "Middle School",
                        facet: "Middle School"
                    },
                    {
                        label: "High School",
                        facet: "High School"
                    },
                    {
                        label: "College (13-14)",
                        facet: "Undergraduate (Lower Division)"
                    },
                    {
                        label: "College (15-16)",
                        facet: "Undergraduate (Upper Division)"
                    },
                    {
                        label: "Graduate / Professional",
                        facet: "Graduate/Professional"
                    },
                    {
                        label: "Vocational / Professional Development Education",
                        facet: "Vocational/Professional Development Education"
                    },
                    {
                        label: "Informal Education",
                        facet: "Informal Education"
                    }
                ]
            },
            {
                // Simple configuration: just indicate the facet category name to display default labels.
                head: {
                    name: "Subject"
                }
            },
            {
                head: {
                    name: "Type",
                    label: "Resource Type" // Define an alternate label for the facet category, if desired:
                },
                // By default, facet labels from the API are displayed.
                // (Optional) provide alternate labels if desired or omit a given facet altogether using omitFromViews:true
                labels: [
                    {
                        omitFromViews: true,
                        facet: "Service"
                    }
                ]
            },
            {
                head: {
                    name: "Audience"
                }
            },

            // ------ ASN Standards -------
            // ASN standards facets include NGSS, NSES, AAAS, and others. A small portion of the NGSS standards are configured below.
            // Tip: To view all ASN standards IDs in the records, remove the orderBy:labels in the config
            // Put the leaf ID in this path to view http://asn.jesandco.org/resources/D10001D0
            {
                head: {
                    // This is a config for a specific category with sub-path (e.g. hv=ASNStandardID:D2454348:S2467517):
                    // Remove the path portion to configure.
                    label: "Disciplinary Core Idea",
                    name: "ASNStandardID:D2454348:S2467517",
                    // Use orderBy Labels (optional) to enforce the order that appears in the labels list and to omit facets that are not in the list
                    // orderBy "labels" | "facets" (default)
                    orderBy: "labels"
                },
                labels: [
                    {
                        // Display a Subtitle heading:
                        label: "Physical Science",
                        isSubtitle: true
                    },

                    {
                        facet: "S2470675",
                        label: "PS1.A: Structure and Properties of Matter"
                    },
                    {
                        label: "PS1.C: Nuclear Processes",
                        facet: "S2471663"
                    },

                    {
                        label: "PS2.B: Types of Interactions",
                        facet: "S2468140"
                    },

                    {
                        label: "PS4.A: Wave Properties",
                        facet: "S2470594"
                    },

                    {
                        label: "PS4.B: Electromagnetic Radiation",
                        facet: "S2470595"
                    },
                    {
                        label: "PS4.C: Information Technologies and Instrumentation",
                        facet: "S2470596"
                    },


                    {
                        // Display a Subtitle heading:
                        label: "Life Science",
                        isSubtitle: true
                    },
                    {
                        label: "HS-LS1: From Molecules to Organisms: Structure and Processes",
                        facet: "S2467904"
                    },
                    {
                        label: "HS-LS1.A: Structure and Function",
                        facet: "S2470616"
                    },
                    {
                        label: "HS-LS1.B: Growth and Development of Organisms",
                        facet: "S2470617"
                    },
                    {
                        label: "HS-LS1.C: Organization for Matter and Energy Flow in Organisms",
                        facet: "S2470531"
                    },
                    {
                        label: "HS-LS2: Ecosystems: Interactions, Energy and Dynamics",
                        facet: "S2467905"
                    },
                    {
                        label: "HS-LS2.A: Interdependent Relationships in Ecosystems",
                        facet: "S2470699"
                    },
                    {
                        label: "HS-LS2:B: Cycles of Matter and Energy Transfer in Ecosystems",
                        facet: "S2471107"
                    },
                    {
                        label: "HS-LS2.C: Ecosystem Dynamics, Functioning, and Resilience",
                        facet: "S2470860"
                    },
                    {
                        label: "HS-LS2.D: Social Interactions and Group Behavior",
                        facet: "S2470824"
                    },
                    {
                        label: "HS-LS3 Heredity: Inheritance and Variation of Traits",
                        facet: "S2467906"
                    },
                    {
                        label: "HS-LS3.A: Inheritance of Traits",
                        facet: "S2470634"
                    },
                    {
                        label: "HS-LS3.B: Variation of Traits",
                        facet: "S2470635"
                    },
                    {
                        label: "HS-LS4: Biological Evolution: Unity and Diversity",
                        facet: "S2454363"
                    },
                    {
                        label: "HS-LS4.A: Evidence of Common Ancestry and Diversity",
                        facet: "S2470861"
                    },
                    {
                        label: "HS-LS4.B: Natural Selection",
                        facet: "S2470862"
                    },
                    {
                        label: "HS-LS4.C: Adaptation",
                        facet: "S2470863"
                    },
                    {
                        label: "HS-LS4.D: Biodiversity and Humans",
                        facet: "S2470715"
                    },

                    {
                        // Display a Subtitle heading:
                        label: "Earth and Space Science",
                        isSubtitle: true
                    },

                    {
                        facet: "S2470649",
                        label: "ESS1.A: The Universe and its Stars"
                    },
                    {
                        facet: "S2470650",
                        label: "ESS1.B: Earth and the Solar System"
                    },

                    {
                        label: "ESS2.C: The Roles of Water in Earth’s Surface Processes",
                        facet: "S2470741OFF"
                    },
                    {
                        label: "ESS2.D: Weather and Climate",
                        facet: "S2470543"
                    },

                    {
                        label: "ESS3.A: Natural Resources",
                        facet: "S2470563"
                    },

                    {
                        label: "HS-ESS3.D: Global Climate Change",
                        facet: "S2471597"
                    }
                ]
            }

            /*
            {
                head: {
                    // This is a config for a specific category with sub-path (e.g. hv=ASNStandardID:D2454348:S2467516):
                    label: "Science and Engineering Practice",
                    name: "ASNStandardID:D2454348:S2467516",
                    // Use orderBy Labels (optional) to enforce the order that appears in the labels list and to omit facets that are not in the list
                    // orderBy "labels" | "facets" (default)
                    orderBy: "labels"
                },
                labels: [

                    // Eample sub-heading in the display  Science and Engineering Practices
                    //{
                    //    label: "Science and Engineering Practices",
                    //    isSubtitle: true
                    //},
                    {
                        label: "Analyzing and Interpreting Data",
                        facet: "S2467520"
                    },
                    {
                        label: "Asking Questions and Defining Problems",
                        facet: "S2470553"
                    },
                    {
                        label: "Constructing Explanations and Designing Solutions",
                        facet: "S2470516"
                    },
                    {
                        label: "Developing and Using Models",
                        facet: "S2470554"
                    },
                    {
                        label: "Engaging in Argument from Evidence",
                        facet: "S2470664"
                    },
                    {
                        label: "Obtaining, Evaluating, and Communicating Information",
                        facet: "S2470555"
                    },
                    {
                        label: "Planning and Carrying Out Investigations",
                        facet: "S2467519"
                    },

                    // Additional Practices not from BioHub (3rd level only)
                    {
                        facet: "S2470665",
                        label: "Science Models, Laws, Mechanisms, and Theories Explain Natural Phenomena"
                    },
                    {
                        facet: "S2470529",
                        label: "Scientific Knowledge is Based on Empirical Evidence"
                    },
                    {
                        facet: "S2471044",
                        label: "Using Mathematics and Computational Thinking"
                    }
                ]
            },


            {
                head: {
                    // This is a config for a specific category with sub-path (e.g. hv=ASNStandardID:D2454348:S2467518):
                    label: "Crosscutting Concept",
                    name: "ASNStandardID:D2454348:S2467518",
                    // Use orderBy Labels (optional) to enforce the order that appears in the labels list and to omit facets that are not in the list
                    // orderBy "labels" | "facets" (default)
                    orderBy: "labels"
                },
                labels: [
                    {
                        facet: "S2470533",
                        label: "Patterns"
                    },
                    {
                        facet: "S2468135",
                        label: "Cause and Effect"
                    },

                    {
                        facet: "S2470872",
                        label: "Scale, Proportion, and Quantity"
                    },

                    {
                        facet: "S2470550",
                        label: "Systems and System Models"
                    },

                    {
                        facet: "S2470683",
                        label: "Energy and Matter"
                    },

                    {
                        facet: "S2470655",
                        label: "Scientific Knowledge Assumes an Order and Consistency in Natural Systems"
                    },

                    {
                        facet: "S2470750",
                        label: "Science Addresses Questions About the Natural and Material World"
                    }
                ]
            }

            */


            /*

             // This config does CCCs to the lowest level as an alternative to the above
             {
             head: {
             // This is a config for a specific category with sub-path (e.g. hv=ASNStandardID:D2454348:S2467518):
             name: "ASNStandardID:D2454348:S2467518",
             label: "Crosscutting Concept",
             // Use orderBy Labels (optional) to enforce the order that appears in the labels list and to omit facets that are not in the list
             // orderBy "labels" | "facets" (default)
             orderBy: "labels"
             },
             labels: [
             {
             //facet: "S2470533",
             label: "Patterns",
             isSubtitle: true
             },
             {
             facet: "S2471495",
             label: "Graphs, charts, and images can be used to identify patterns in data"
             },
             {
             facet: "S2471338",
             label: "Graphs and charts can be used to identify patterns in data"
             },
             {
             facet: "S2471127",
             label: "Similarities and differences in patterns can be used to sort, classify, communicate and analyze simple rates of change for natural phenomena"
             },
             {
             facet: "S2470995",
             label: "Patterns can be used as evidence to support an explanation"
             },
             {
             facet: "S2470967",
             label: "Similarities and differences in patterns can be used to sort and classify designed products"
             },
             {
             facet: "S2470845",
             label: "Similarities and differences in patterns can be used to sort and classify natural phenomena"
             },
             {
             facet: "S2470802",
             label: "Patterns of change can be used to make predictions"
             },
             {
             facet: "S2470751",
             label: "Patterns in the natural world can be observed"
             },
             {
             facet: "S2470685",
             label: "Patterns in the natural and human designed world can be observed"
             },
             {
             facet: "S2470551",
             label: "Patterns in the natural world can be observed, used to describe phenomena, and used as evidence"
             },
             {
             facet: "S2470534",
             label: "Patterns in the natural and human designed world can be observed and used as evidence"
             },
             {
             facet: "S2472017",
             label: "Empirical evidence is needed to identify patterns"
             },
             {
             facet: "S2471678",
             label: "Different patterns may be observed at each of the scales at which a system is studied and can provide evidence for causality in explanations of phenomena"
             },
             {
             facet: "S2471572",
             label: "Patterns in rates of change and other numerical relationships can provide information about natural systems"
             },
             {
             facet: "S2471410",
             label: "Patterns can be used to identify cause and effect relationships"
             },
             {
             facet: "S2471227",
             label: "Macroscopic patterns are related to the nature of microscopic and atomic-level structure"
             },


             {
             //facet: "S2470550",
             label: "Systems and System Models",
             isSubtitle: true
             },
             {
             facet: "S2471528",
             label: "Models can be used to represent systems and their interactions"
             },
             {
             facet: "S2471266",
             label: "Models can be used to represent systems and their interactions—such as inputs, processes and outputs—and energy and matter flows within systems"
             },
             {
             facet: "S2472125",
             label: "Empirical evidence is required to differentiate between cause and correlation and make claims about specific causes and effects"
             },
             {
             facet: "S2471809",
             label: "Models (e.g., physical, mathematical, computer models) can be used to simulate systems and interactions—including energy, matter, and information flows—within and between systems at different scales"
             },
             {
             facet: "S2471766",
             label: "Models can be used to predict the behavior of a system, but these predictions have limited precision and reliability due to the assumptions and approximations inherent in models"
             },
             {
             facet: "S2471765",
             label: "When investigating or describing a system, the boundaries and initial conditions of the system need to be defined and their inputs and outputs analyzed and described using models"
             },
             {
             facet: "S2471726",
             label: "When investigating or describing a system, the boundaries and initial conditions of the system need to be defined"
             },
             {
             facet: "S2471575",
             label: "Models can be used to represent systems and their interactions—such as inputs, processes and outputs— and energy, matter, and information flows within systems"
             },
             {
             facet: "S2471389",
             label: "Systems may interact with other systems; they may have sub-systems and be a part of larger complex systems"
             },
             {
             facet: "S2470878",
             label: "A system can be described in terms of its components and their interactions"
             },
             {
             facet: "S2470552",
             label: "Systems in the natural and designed world have parts that work together"
             },


             {
             //facet: "S2468135",
             label: "Cause and Effect",
             isSubtitle: true
             },
             {
             facet: "S2471386",
             label: "Cause and effect relationships may be used to predict phenomena in natural systems"
             },
             {
             facet: "S2470526",
             label: "Events have causes that generate observable patterns"
             },


             {
             //facet: "S2470683",
             label: "Energy and Matter",
             isSubtitle: true
             },
             {
             facet: "S2471391",
             label: "Within a natural system, the transfer of energy drives the motion and/or cycling of matter"
             },


             {
             //facet: "S2470872",
             label: "Scale, Proportion, and Quantity",
             isSubtitle: true
             },
             {
             facet: "S2471229",
             label: "Time, space, and energy phenomena can be observed at various scales using models to study systems that are too large or too small"
             },


             {
             //facet: "S2470655",
             label: "Scientific Knowledge Assumes an Order and Consistency in Natural Systems",
             isSubtitle: true
             },
             {
             facet: "S2471498",
             label: "Science assumes that objects and events in natural systems occur in consistent patterns that are understandable through measurement and observation."
             },


             {
             //facet: "S2470750",
             label: "Science Addresses Questions About the Natural and Material World",
             isSubtitle: true
             },
             {
             facet: "S2471157",
             label: "Science findings are limited to questions that can be answered with empirical evidence"
             }
             ]
             }
             */


        ]
    }
};