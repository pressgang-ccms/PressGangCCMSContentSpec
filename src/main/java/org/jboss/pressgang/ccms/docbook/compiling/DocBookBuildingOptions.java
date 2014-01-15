package org.jboss.pressgang.ccms.docbook.compiling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocBookBuildingOptions implements Serializable {
    private static final long serialVersionUID = -3481034970109486054L;
    private Boolean suppressContentSpecPage = false;
    private Boolean insertBugLinks = true;
    private Boolean publicanShowRemarks = false;
    private Boolean ignoreMissingCustomInjections = true;
    private Boolean suppressErrorsPage = false;
    private Boolean insertSurveyLink = false;
    private Boolean insertEditorLinks = false;
    private String buildName = null;
    private List<String> injectionTypes = new ArrayList<String>();
    private Boolean injection = true;
    private Map<String, String> overrides = new HashMap<String, String>();
    private Map<String, String> publicanCfgOverrides = new HashMap<String, String>();
    private Boolean allowEmptySections = false;
    private Boolean showReportPage = false;
    private String locale = null;
    private String commonContentDirectory = null;
    private String outputLocale = null;
    private Boolean draft = false;
    private List<String> revisionMessages = null;
    private Boolean useLatestVersions = false;
    private Boolean flattenTopics = false;
    private Boolean flatten = false;
    private Boolean forceInjectBugLinks = false;
    private Boolean serverBuild = false;
    private Integer maxRevision = null;

    public DocBookBuildingOptions() {

    }

    public String getBuildName() {
        return buildName;
    }

    public void setBuildName(final String buildName) {
        this.buildName = buildName;
    }

    public Boolean getInsertBugLinks() {
        return insertBugLinks;
    }

    public void setInsertBugLinks(Boolean insertBugLinks) {
        this.insertBugLinks = insertBugLinks;
    }

    public Boolean getSuppressContentSpecPage() {
        return suppressContentSpecPage;
    }

    public void setSuppressContentSpecPage(Boolean suppressContentSpecPage) {
        this.suppressContentSpecPage = suppressContentSpecPage;
    }

    public Boolean getInsertEditorLinks() {
        return insertEditorLinks;
    }

    public void setInsertEditorLinks(final Boolean insertEditorLinks) {
        this.insertEditorLinks = insertEditorLinks;
    }

    public void setPublicanShowRemarks(final Boolean publicanShowRemarks) {
        this.publicanShowRemarks = publicanShowRemarks;
    }

    public Boolean getPublicanShowRemarks() {
        return publicanShowRemarks;
    }

    public Boolean getIgnoreMissingCustomInjections() {
        return ignoreMissingCustomInjections;
    }

    public void setIgnoreMissingCustomInjections(final Boolean ignoreMissingCustomInjections) {
        this.ignoreMissingCustomInjections = ignoreMissingCustomInjections;
    }

    public Boolean getSuppressErrorsPage() {
        return suppressErrorsPage;
    }

    public void setSuppressErrorsPage(final Boolean suppressErrorsPage) {
        this.suppressErrorsPage = suppressErrorsPage;
    }

    public Boolean getInsertSurveyLink() {
        return insertSurveyLink;
    }

    public void setInsertSurveyLink(final Boolean insertSurveyLink) {
        this.insertSurveyLink = insertSurveyLink;
    }

    public List<String> getInjectionTypes() {
        return injectionTypes;
    }

    public void setInjectionTypes(final List<String> injectionTypes) {
        this.injectionTypes = injectionTypes;
    }

    public boolean getInjection() {
        return injection;
    }

    public void setInjection(final Boolean injection) {
        this.injection = injection;
    }

    public Map<String, String> getOverrides() {
        return overrides;
    }

    public void setOverrides(final Map<String, String> overrides) {
        this.overrides = overrides;
    }

    public Map<String, String> getPublicanCfgOverrides() {
        return publicanCfgOverrides;
    }

    public void setPublicanCfgOverrides(final Map<String, String> publicanCfgOverrides) {
        this.publicanCfgOverrides = publicanCfgOverrides;
    }

    public boolean isAllowEmptySections() {
        return allowEmptySections;
    }

    public void setAllowEmptySections(final Boolean allowEmptySections) {
        this.allowEmptySections = allowEmptySections;
    }

    public Boolean getShowReportPage() {
        return showReportPage;
    }

    public void setShowReportPage(final Boolean showReportPage) {
        this.showReportPage = showReportPage;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getCommonContentDirectory() {
        return commonContentDirectory;
    }

    public void setCommonContentDirectory(final String commonContentDirectory) {
        this.commonContentDirectory = commonContentDirectory;
    }

    public String getOutputLocale() {
        return outputLocale;
    }

    public void setOutputLocale(String outputLocale) {
        this.outputLocale = outputLocale;
    }

    public Boolean getDraft() {
        return draft;
    }

    public void setDraft(final Boolean draft) {
        this.draft = draft;
    }

    public List<String> getRevisionMessages() {
        return revisionMessages;
    }

    public void setRevisionMessages(final List<String> revisionMessage) {
        this.revisionMessages = revisionMessage;
    }

    public Boolean getUseLatestVersions() {
        return useLatestVersions;
    }

    public void setUseLatestVersions(Boolean useLatestVersions) {
        this.useLatestVersions = useLatestVersions;
    }

    public Boolean getFlattenTopics() {
        return flattenTopics;
    }

    public void setFlattenTopics(Boolean flattenTopics) {
        this.flattenTopics = flattenTopics;
    }

    public Boolean getForceInjectBugLinks() {
        return forceInjectBugLinks;
    }

    public void setForceInjectBugLinks(Boolean forceInjectBugLinks) {
        this.forceInjectBugLinks = forceInjectBugLinks;
    }

    public Boolean isServerBuild() {
        return serverBuild;
    }

    public void setServerBuild(Boolean serverBuild) {
        this.serverBuild = serverBuild;
    }

    public Boolean getFlatten() {
        return flatten;
    }

    public void setFlatten(Boolean flatten) {
        this.flatten = flatten;
    }

    public Integer getMaxRevision() {
        return maxRevision;
    }

    public void setMaxRevision(Integer maxRevision) {
        this.maxRevision = maxRevision;
    }
}
