/*******************************************************************************
 * Copyright (c) 2010-2011 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.service.core.model;

import com.liferay.ide.eclipse.service.core.model.internal.ColumnImageService;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Gregory Amerson
 */
@GenerateImpl
@Service(impl = ColumnImageService.class)
public interface IColumn extends IModelElement {

    ModelElementType TYPE = new ModelElementType( IColumn.class );
    
	// *** Name ***
    
    @XmlBinding(path = "@name")
	@Label(standard = "&name")
	@Required
	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

    Value<String> getName();

	void setName(String value);

	// *** Db Name ***

	@XmlBinding(path = "@db-name")
	@Label(standard = "&db name")
	ValueProperty PROP_DB_NAME = new ValueProperty(TYPE, "DbName");

	Value<String> getDbName();

	void setDbName(String value);

	// *** Type ***

	@Label(standard = "type")
	@XmlBinding(path = "@type")
	@Required
	@PossibleValues( values = { "String", "long", "boolean", "int", "double", "Date", "Collection" }, invalidValueMessage = "{0} is not a valid type." )
	ValueProperty PROP_TYPE = new ValueProperty(TYPE, "Type");

	Value<String> getType();

	void setType(String value);

	// *** Primary ***

	@Type(base = Boolean.class)
	@Label(standard = "&primary")
	@XmlBinding(path = "@primary")
	@DefaultValue(text = "false")
	ValueProperty PROP_PRIMARY = new ValueProperty(TYPE, "Primary");

	Value<Boolean> isPrimary();

	void setPrimary(String value);

	void setPrimary(Boolean value);

	// *** Filter Primary ***

	@Type(base = Boolean.class)
	@Label(standard = "&filter primary")
	@XmlBinding(path = "@filter-primary")
	@DefaultValue(text = "false")
	ValueProperty PROP_FILTER_PRIMARY = new ValueProperty(TYPE, "FilterPrimary");

	Value<Boolean> isFilterPrimary();

	void setFilterPrimary(String value);

	void setFilterPrimary(Boolean value);

	// *** Entity ***

	@XmlBinding(path = "@entity")
	@Label(standard = "&entity")
	ValueProperty PROP_ENTITY = new ValueProperty(TYPE, "Entity");

	Value<String> getEntity();

	void setEntity(String value);

	// *** Mapping Key ***

	@XmlBinding(path = "@mapping-key")
	@Label(standard = "&mapping key")
	ValueProperty PROP_MAPPING_KEY = new ValueProperty(TYPE, "MappingKey");

	Value<String> getMappingKey();

	void setMappingKey(String value);

	// *** Mapping Table ***

	@XmlBinding(path = "@mapping-table")
	@Label(standard = "&mapping table")
	ValueProperty PROP_MAPPING_TABLE = new ValueProperty(TYPE, "MappingTable");

	Value<String> getMappingTable();

	void setMappingTable(String value);

	// ** Id Type ***
	@Label(standard = "id type")
	@XmlBinding(path = "@id-type")
	@PossibleValues(values = { "class", "increment", "identity", "sequence" }, invalidValueMessage = "{0} is not a valid ID type.")
	ValueProperty PROP_ID_TYPE = new ValueProperty(TYPE, "IdType");

	Value<String> getIdType();

	void setIdType(String value);

	// *** Id Param ***

	@XmlBinding(path = "@id-param")
	@Label(standard = "&id param")
	ValueProperty PROP_ID_PARAM = new ValueProperty(TYPE, "IdParam");

	Value<String> getIdParam();

	void setIdParam(String value);

	// *** Convert Null ***

	@Type(base = Boolean.class)
	@Label(standard = "&convert null")
	@XmlBinding(path = "@convert-null")
	@DefaultValue(text = "true")
	ValueProperty PROP_CONVERT_NULL = new ValueProperty(TYPE, "ConvertNull");

	Value<Boolean> isConvertNull();

	void setConvertNull(String value);

	void setConvertNull(Boolean value);

	@Type( base = Boolean.class )
	@Label( standard = "&lazy" )
	@XmlBinding( path = "@lazy" )
	@DefaultValue( text = "true" )
	ValueProperty PROP_LAZY = new ValueProperty( TYPE, "Lazy" );

	Value<Boolean> isLazy();

	void setLazy( String value );

	void setLazy( Boolean value );

	// *** Localized ***

	@Type(base = Boolean.class)
	@Label(standard = "&localized")
	@XmlBinding(path = "@localized")
	@DefaultValue(text = "false")
	ValueProperty PROP_LOCALIZED = new ValueProperty(TYPE, "Localized");

	Value<Boolean> isLocalized();

	void setLocalized(String value);

	void setLocalized(Boolean value);

	// *** Json Enabled

	@Type( base = Boolean.class )
	@Label( standard = "&JSON enabled" )
	@XmlBinding( path = "@json-enabled" )
	@DefaultValue( text = "true" )
	ValueProperty PROP_JSON_ENABLED = new ValueProperty( TYPE, "JsonEnabled" );

	Value<Boolean> isJsonEnabled();

	void setJsonEnabled( String value );

	void setJsonEnabled( Boolean value );

	// *** Accessor ***

	@Type( base = Boolean.class )
	@XmlBinding( path = "@accessor" )
	@Label( standard = "&accessor" )
	@DefaultValue( text = "false" )
	@Documentation( content = "This [b]accessor[/b] value specifies whether or not to generate an accessor for this column. This accessor will provide a fast and type-safe way to access column value." )
	ValueProperty PROP_ACCESSOR = new ValueProperty( TYPE, "Accessor" );

	Value<Boolean> getAccessor();

	void setAccessor( String value );

	void setAccessor( Boolean value );
}
