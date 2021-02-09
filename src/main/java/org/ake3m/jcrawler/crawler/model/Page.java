package org.ake3m.jcrawler.crawler.model;

import lombok.Getter;

import java.util.Set;

import static org.ake3m.jcrawler.common.StringUtils.asBulletedList;

@Getter
public class Page {
    private final String parent;
    private final Set<String> internalLinks;
    private final Set<String> externalLinks;
    private final Set<String> staticAssets;

    public Page(String parent,
                Set<String> internalLinks,
                Set<String> externalLinks,
                Set<String> staticAssets) {
        this.parent = parent;
        this.internalLinks = internalLinks;
        this.externalLinks = externalLinks;
        this.staticAssets = staticAssets;

    }


    @Override
    public String toString() {
        return """
                >>>>>>>>>>>>>>>>>>>>>>>>>>
                
                PARENT
                ======
                %s
                
                Internal Links
                --------------
                %s
                                
                External Links
                --------------
                %s
                                
                Static assets
                --------------
                %s
                
                >>>>>>>>>>>>>>>>>>>>>>>>>>            
                """
                .formatted(
                        parent,
                        asBulletedList(internalLinks),
                        asBulletedList(externalLinks),
                        asBulletedList(staticAssets)
                );
    }
}
