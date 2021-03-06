/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014  Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.annatala.pixelponies.windows;

import com.annatala.pixelponies.android.util.GuiProperties;
import com.annatala.pixelponies.android.R;
import com.annatala.opengl.SmartTexture;
import com.annatala.opengl.TextureCache;
import com.annatala.noosa.Game;
import com.annatala.noosa.Group;
import com.annatala.noosa.Image;
import com.annatala.noosa.Text;
import com.annatala.noosa.TextureFilm;
import com.annatala.pixelponies.Assets;
import com.annatala.pixelponies.Dungeon;
import com.annatala.pixelponies.Statistics;
import com.annatala.pixelponies.actors.buffs.Buff;
import com.annatala.pixelponies.actors.hero.Hero;
import com.annatala.pixelponies.scenes.GameScene;
import com.annatala.pixelponies.scenes.PixelScene;
import com.annatala.pixelponies.ui.BuffIndicator;
import com.annatala.pixelponies.ui.RedButton;
import com.annatala.utils.Utils;
import com.annatala.pixelponies.windows.elements.LabeledTab;
import com.annatala.pixelponies.windows.elements.Tab;

import java.util.Locale;

public class WndHero extends WndTabbed {
	
	private static final String TXT_STATS	= Game.getVar(R.string.WndHero_Stats);
	private static final String TXT_BUFFS	= Game.getVar(R.string.WndHero_Buffs);
	
	private static final String TXT_EXP		= Game.getVar(R.string.WndHero_Exp);
	private static final String TXT_HONESTY		= Game.getVar(R.string.WndHero_Honesty);
	private static final String TXT_LOYALTY		= Game.getVar(R.string.WndHero_Loyalty);
	private static final String TXT_LAUGHTER		= Game.getVar(R.string.WndHero_Laughter);
	private static final String TXT_GENEROSITY		= Game.getVar(R.string.WndHero_Generosity);
	private static final String TXT_KINDNESS		= Game.getVar(R.string.WndHero_Kindness);
	private static final String TXT_MAGIC		= Game.getVar(R.string.WndHero_Magic);
	private static final String TXT_HEALTH	= Game.getVar(R.string.WndHero_Health);
	private static final String TXT_GOLD	= Game.getVar(R.string.WndHero_Gold);
	private static final String TXT_DEPTH	= Game.getVar(R.string.WndHero_Depth);
	
	private static final int WIDTH		= 100;
	private static final int TAB_WIDTH	= 50;
	
	private StatsTab stats;
	private BuffsTab buffs;
	
	private SmartTexture icons;
	private TextureFilm film;
	
	public WndHero() {
		
		super();
		
		icons = TextureCache.get( Assets.BUFFS_LARGE );
		film = new TextureFilm( icons, 16, 16 );
		
		stats = new StatsTab();
		add( stats );
		
		buffs = new BuffsTab();
		add( buffs );
		
		add( new LabeledTab( this, TXT_STATS ) {
			public void select( boolean value ) {
				super.select( value );
				stats.setVisible(stats.active = selected);
			}
		} );
		add( new LabeledTab( this, TXT_BUFFS ) {
			public void select( boolean value ) {
				super.select( value );
				buffs.setVisible(buffs.active = selected);
			}
		} );
		for (Tab tab : tabs) {
			tab.setSize( TAB_WIDTH, tabHeight() );
		}
		
		resize( WIDTH, (int)Math.max( stats.height(), buffs.height() ) );
		
		select( 0 );
	}
	
	private class StatsTab extends Group {
		
		//Removido o "Static" para poder definir valor a partir ds resouces
		private final String TXT_TITLE     = Game.getVar(R.string.WndHero_StaTitle);
		private final String TXT_CATALOGUS = Game.getVar(R.string.WndHero_StaCatalogus);
		private final String TXT_JOURNAL   = Game.getVar(R.string.WndHero_StaJournal);
		
		private static final int GAP = 5;
		
		private float pos;
		
		public StatsTab() {
			
			Hero hero = Dungeon.hero; 

			Text title = PixelScene.createText( 
				Utils.format( TXT_TITLE, hero.lvl(), hero.className() ).toUpperCase( Locale.ENGLISH ), GuiProperties.titleFontSize());
			title.hardlight( TITLE_COLOR );
			title.measure();
			add( title );
			
			RedButton btnCatalogus = new RedButton( TXT_CATALOGUS ) {
				@Override
				protected void onClick() {
					hide();
					GameScene.show( new WndCatalogus() );
				}
			};
			btnCatalogus.setRect( 0, title.y + title.height(), btnCatalogus.reqWidth() + 2, btnCatalogus.reqHeight() + 2 );
			add( btnCatalogus );
			
			RedButton btnJournal = new RedButton( TXT_JOURNAL ) {
				@Override
				protected void onClick() {
					hide();
					GameScene.show( new WndJournal() );
				}
			};
			btnJournal.setRect( 
				btnCatalogus.right() + 1, btnCatalogus.top(), 
				btnJournal.reqWidth() + 2, btnJournal.reqHeight() + 2 );
			add( btnJournal );
			
			pos = btnCatalogus.bottom() + GAP;
			
			statSlot( TXT_HONESTY, hero.effectiveHonesty() );
			statSlot( TXT_LOYALTY, hero.effectiveLoyalty());
			statSlot( TXT_LAUGHTER, hero.effectiveLaughter());
			statSlot( TXT_GENEROSITY, hero.effectiveGenerosity());
			statSlot( TXT_KINDNESS, hero.effectiveKindness());
			statSlot( TXT_MAGIC, hero.effectiveMagic());

			statSlot( TXT_HEALTH, hero.hp() + "/" + hero.ht() );
			statSlot( TXT_EXP, hero.getExp() + "/" + hero.maxExp() );

			pos += GAP;
			
			statSlot( TXT_GOLD, Statistics.goldCollected );
			statSlot( TXT_DEPTH, Statistics.deepestFloor );
			
			pos += GAP;
		}
		
		private void statSlot( String label, String value ) {
			
			Text txt = PixelScene.createText( label, GuiProperties.regularFontSize() );
			txt.y = pos;
			add( txt );
			
			txt = PixelScene.createText( value, GuiProperties.regularFontSize() );
			txt.measure();
			txt.x = PixelScene.align( WIDTH * 0.65f );
			txt.y = pos;
			add( txt );
			
			pos += GAP + txt.baseLine();
		}
		
		private void statSlot( String label, int value ) {
			statSlot( label, Integer.toString( value ) );
		}
		
		public float height() {
			return pos;
		}
	}
	
	private class BuffsTab extends Group {
		
		private static final int GAP = 2;
		
		private float pos;
		
		public BuffsTab() {
			for (Buff buff : Dungeon.hero.buffs()) {
				buffSlot( buff );
			}
		}
		
		private void buffSlot( Buff buff ) {
			
			int index = buff.icon();
			
			if (index != BuffIndicator.NONE) {
				
				Image icon = new Image( icons );
				icon.frame( film.get( index ) );
				icon.y = pos;
				add( icon );
				
				Text txt = PixelScene.createText( buff.toString(), GuiProperties.regularFontSize() );
				txt.x = icon.width + GAP;
				txt.y = pos + (int)(icon.height - txt.baseLine()) / 2;
				add( txt );
				
				pos += GAP + icon.height;
			}
		}
		
		public float height() {
			return pos;
		}
	}
}
